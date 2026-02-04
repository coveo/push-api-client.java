package com.coveo.pushapiclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

/**
 * Integration tests for file container rotation when pushing large amounts of data. These tests
 * verify the end-to-end flow from UpdateStreamService through CatalogStreamUploadHandler to
 * PlatformClient, using a small batch size to trigger rotation without needing large test data.
 *
 * <p>Key architectural pattern: Each batch creates its own file container via
 * CatalogStreamUploadHandler. The handler executes create→upload→push for each uploadAndPush()
 * call, ensuring container rotation per batch.
 */
public class FileContainerRotationIntegrationTest {

  private static final int SMALL_BATCH_SIZE = 1000;
  private static final String SOURCE_ID = "test-source-id";
  private static final String ORG_ID = "test-org";
  private static final String API_KEY = "test-api-key";

  private PlatformClient platformClient;
  private StreamEnabledSource source;
  private AtomicInteger containerCounter;

  @Before
  public void setUp() throws IOException, InterruptedException {
    platformClient = mock(PlatformClient.class);
    source = mock(StreamEnabledSource.class);
    containerCounter = new AtomicInteger(0);

    doReturn(SOURCE_ID).when(source).getId();
    doReturn(ORG_ID).when(source).getOrganizationId();
    doReturn(API_KEY).when(source).getApiKey();
    doReturn(new PlatformUrl(Environment.PRODUCTION, Region.US)).when(source).getPlatformUrl();

    doAnswer(invocation -> createContainerResponse()).when(platformClient).createFileContainer();
    doReturn(createGenericResponse())
        .when(platformClient)
        .uploadContentToFileContainer(any(), anyString());
    doReturn(createGenericResponse())
        .when(platformClient)
        .pushFileContainerContentToStreamSource(anyString(), any());
  }

  @Test
  public void shouldCreateMultipleContainersWhenDataExceedsBatchSize() throws Exception {
    UpdateStreamServiceInternal service = createServiceWithSmallBatchSize();

    service.addOrUpdate(createDocument("doc1", 600));
    service.addOrUpdate(createDocument("doc2", 600));
    service.addOrUpdate(createDocument("doc3", 600));
    service.addOrUpdate(createDocument("doc4", 600));
    service.close();

    verify(platformClient, times(4)).createFileContainer();
    verify(platformClient, times(4)).pushFileContainerContentToStreamSource(anyString(), any());
  }

  @Test
  public void shouldCreateSingleContainerWhenDataFitsInOneBatch() throws Exception {
    UpdateStreamServiceInternal service = createServiceWithSmallBatchSize();

    service.addOrUpdate(createDocument("doc1", 100));
    service.addOrUpdate(createDocument("doc2", 100));
    service.close();

    verify(platformClient, times(1)).createFileContainer();
    verify(platformClient, times(1)).pushFileContainerContentToStreamSource(anyString(), any());
  }

  @Test
  public void shouldHandleMixedOperationsWithRotation() throws Exception {
    UpdateStreamServiceInternal service = createServiceWithSmallBatchSize();

    service.addOrUpdate(createDocument("doc1", 400));
    service.delete(new DeleteDocument("doc2"));
    service.addPartialUpdate(createPartialUpdate("doc3", 400));
    service.addOrUpdate(createDocument("doc4", 400));
    service.close();

    verify(platformClient, times(3)).createFileContainer();
    verify(platformClient, times(3)).pushFileContainerContentToStreamSource(anyString(), any());
  }

  @Test
  public void shouldUseUniqueContainerIdForEachBatch() throws Exception {
    UpdateStreamServiceInternal service = createServiceWithSmallBatchSize();

    service.addOrUpdate(createDocument("doc1", 600));
    service.addOrUpdate(createDocument("doc2", 600));
    service.addOrUpdate(createDocument("doc3", 600));
    service.close();

    ArgumentCaptor<FileContainer> containerCaptor = ArgumentCaptor.forClass(FileContainer.class);
    verify(platformClient, times(3))
        .pushFileContainerContentToStreamSource(anyString(), containerCaptor.capture());

    assertEquals("container-1", containerCaptor.getAllValues().get(0).fileId);
    assertEquals("container-2", containerCaptor.getAllValues().get(1).fileId);
    assertEquals("container-3", containerCaptor.getAllValues().get(2).fileId);
  }

  @Test
  public void shouldPushImmediatelyWhenBatchSizeExceeded() throws Exception {
    UpdateStreamServiceInternal service = createServiceWithSmallBatchSize();

    service.addOrUpdate(createDocument("doc1", 600));
    verify(platformClient, times(0)).pushFileContainerContentToStreamSource(anyString(), any());

    service.addOrUpdate(createDocument("doc2", 600));
    verify(platformClient, times(1)).pushFileContainerContentToStreamSource(anyString(), any());

    service.addOrUpdate(createDocument("doc3", 600));
    verify(platformClient, times(2)).pushFileContainerContentToStreamSource(anyString(), any());

    service.close();
    verify(platformClient, times(3)).pushFileContainerContentToStreamSource(anyString(), any());
  }

  @Test
  public void shouldHandleLargeNumberOfDocumentsWithRotation() throws Exception {
    UpdateStreamServiceInternal service = createServiceWithSmallBatchSize();

    for (int i = 0; i < 20; i++) {
      service.addOrUpdate(createDocument("doc" + i, 200));
    }
    service.close();

    int expectedContainers = 10;
    verify(platformClient, times(expectedContainers)).createFileContainer();
    verify(platformClient, times(expectedContainers))
        .pushFileContainerContentToStreamSource(anyString(), any());
  }

  @Test
  public void shouldNeverPushMultipleBatchesToSameContainer() throws Exception {
    Map<String, Integer> pushCountPerContainer = new HashMap<>();
    List<String> containerCreationOrder = new ArrayList<>();

    doAnswer(
            invocation -> {
              HttpResponse<String> response = createContainerResponse();
              String fileId = "container-" + containerCounter.get();
              containerCreationOrder.add(fileId);
              pushCountPerContainer.put(fileId, 0);
              return response;
            })
        .when(platformClient)
        .createFileContainer();

    doAnswer(
            invocation -> {
              FileContainer container = invocation.getArgument(1);
              int currentCount = pushCountPerContainer.getOrDefault(container.fileId, 0);
              pushCountPerContainer.put(container.fileId, currentCount + 1);
              return createGenericResponse();
            })
        .when(platformClient)
        .pushFileContainerContentToStreamSource(anyString(), any());

    UpdateStreamServiceInternal service = createServiceWithSmallBatchSize();

    for (int i = 0; i < 10; i++) {
      service.addOrUpdate(createDocument("doc" + i, 400));
    }
    service.close();

    for (Map.Entry<String, Integer> entry : pushCountPerContainer.entrySet()) {
      assertEquals(
          "Container "
              + entry.getKey()
              + " should receive exactly 1 push, but received "
              + entry.getValue(),
          Integer.valueOf(1),
          entry.getValue());
    }

    assertTrue("Should have created multiple containers", containerCreationOrder.size() > 1);
  }

  private UpdateStreamServiceInternal createServiceWithSmallBatchSize() {
    CatalogStreamUploadHandler handler = new CatalogStreamUploadHandler(source, platformClient);
    StreamDocumentUploadQueue queue = new StreamDocumentUploadQueue(handler, SMALL_BATCH_SIZE);
    org.apache.logging.log4j.Logger logger =
        org.apache.logging.log4j.LogManager.getLogger(getClass());
    return new UpdateStreamServiceInternal(source, queue, platformClient, logger);
  }

  private DocumentBuilder createDocument(String id, int dataSize) {
    return new DocumentBuilder("https://example.com/" + id, "Title " + id)
        .withData(generateData(dataSize));
  }

  private PartialUpdateDocument createPartialUpdate(String id, int dataSize) {
    return new PartialUpdateDocument(
        "https://example.com/" + id,
        PartialUpdateOperator.FIELDVALUEREPLACE,
        "field",
        generateData(dataSize));
  }

  private String generateData(int size) {
    byte[] bytes = new byte[size];
    for (int i = 0; i < size; i++) {
      bytes[i] = 65;
    }
    return new String(bytes);
  }

  @SuppressWarnings("unchecked")
  private HttpResponse<String> createContainerResponse() {
    HttpResponse<String> response = mock(HttpResponse.class);
    int id = containerCounter.incrementAndGet();
    String responseBody =
        String.format(
            "{\"uploadUri\": \"https://upload.uri/container-%d\", "
                + "\"fileId\": \"container-%d\"}",
            id, id);
    doReturn(responseBody).when(response).body();
    return response;
  }

  @SuppressWarnings("unchecked")
  private HttpResponse<String> createGenericResponse() {
    HttpResponse<String> response = mock(HttpResponse.class);
    doReturn("{\"status\": \"ok\"}").when(response).body();
    return response;
  }
}
