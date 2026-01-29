package com.coveo.pushapiclient;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coveo.pushapiclient.exceptions.NoOpenFileContainerException;
import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import org.apache.logging.log4j.core.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class UpdateStreamServiceInternalTest {

  private static final String SOURCE_ID = "my-source-id";
  @Mock private StreamEnabledSource source;
  @Mock private PlatformClient platformClient;
  @Mock private StreamDocumentUploadQueue queue;
  @Mock private HttpResponse<String> httpResponse;
  @Mock private Logger logger;

  @InjectMocks private UpdateStreamServiceInternal service;

  private DocumentBuilder documentA;
  private DocumentBuilder documentB;
  private DeleteDocument deleteDocumentA;
  private DeleteDocument deleteDocumentB;
  private PartialUpdateDocument partialUpdateDocumentA;
  private PartialUpdateDocument partialUpdateDocumentB;

  private AutoCloseable closeable;

  @Before
  public void setUp() throws Exception {
    documentA = new DocumentBuilder("https://my.document.uri?ref=1", "My first document title");
    documentB = new DocumentBuilder("https://my.document.uri?ref=2", "My second document title");
    deleteDocumentA = new DeleteDocument("https://my.document.uri?ref=3");
    deleteDocumentB = new DeleteDocument("https://my.document.uri?ref=4");
    partialUpdateDocumentA =
        new PartialUpdateDocument(
            "https://my.document.uri?ref=5",
            PartialUpdateOperator.FIELDVALUEREPLACE,
            "fieldA",
            "valueA");
    partialUpdateDocumentB =
        new PartialUpdateDocument(
            "https://my.document.uri?ref=6",
            PartialUpdateOperator.FIELDVALUEREPLACE,
            "fieldB",
            "valueB");

    closeable = MockitoAnnotations.openMocks(this);

    when(httpResponse.body())
        .thenReturn("{\"uploadUri\": \"https://upload.uri\", \"fileId\": \"file-id\"}");
    when(platformClient.createFileContainer()).thenReturn(httpResponse);
    when(source.getId()).thenReturn(SOURCE_ID);
  }

  @After
  public void closeService() throws Exception {
    closeable.close();
  }

  @Test
  public void addOrUpdateShouldNotCreateFileContainer() throws IOException, InterruptedException {
    service.addOrUpdate(documentA);
    service.addOrUpdate(documentB);

    verify(this.platformClient, times(0)).createFileContainer();
  }

  @Test
  public void addOrUpdateAndPartialAndDeleteShouldAddDocumentsToQueue()
      throws IOException, InterruptedException {
    service.addOrUpdate(documentA);
    service.addOrUpdate(documentB);
    service.delete(deleteDocumentA);
    service.addPartialUpdate(partialUpdateDocumentA);
    service.addPartialUpdate(partialUpdateDocumentB);

    verify(queue, times(1)).add(documentA);
    verify(queue, times(1)).add(documentB);
    verify(queue, times(1)).add(deleteDocumentA);
    verify(queue, times(1)).add(partialUpdateDocumentA);
    verify(queue, times(1)).add(partialUpdateDocumentB);
  }

  @Test
  public void deleteShouldNotCreateFileContainer() throws IOException, InterruptedException {
    service.delete(deleteDocumentA);
    service.delete(deleteDocumentB);

    verify(this.platformClient, times(0)).createFileContainer();
  }

  @Test
  public void partialUpdateShouldNotCreateFileContainer() throws IOException, InterruptedException {
    service.addPartialUpdate(partialUpdateDocumentA);
    service.addPartialUpdate(partialUpdateDocumentB);

    verify(this.platformClient, times(0)).createFileContainer();
  }

  @Test
  public void closeShouldCallFlushAndPush()
      throws IOException, InterruptedException, NoOpenFileContainerException {
    when(queue.isEmpty()).thenReturn(false);
    when(queue.flushAndPush()).thenReturn(httpResponse);

    service.addOrUpdate(documentA);
    service.close();

    verify(queue, times(1)).flushAndPush();
  }

  @Test
  public void closeShouldNotCallFlushAndPushWhenQueueIsEmpty()
      throws IOException, InterruptedException, NoOpenFileContainerException {
    when(queue.isEmpty()).thenReturn(true);

    service.close();

    verify(queue, times(0)).flushAndPush();
  }

  @Test
  public void createUploadAndPushShouldCreateContainerUploadAndPush()
      throws IOException, InterruptedException {
    StreamUpdate streamUpdate =
        new StreamUpdate(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    service.createUploadAndPush(streamUpdate);

    verify(platformClient, times(1)).createFileContainer();
    verify(platformClient, times(1))
        .uploadContentToFileContainer(any(FileContainer.class), any(String.class));
    verify(platformClient, times(1))
        .pushFileContainerContentToStreamSource(eq(SOURCE_ID), any(FileContainer.class));
  }

  @Test
  public void createUploadAndPushShouldUseNewContainerForEachCall()
      throws IOException, InterruptedException {
    HttpResponse<String> response1 = createMockHttpResponse("container-1");
    HttpResponse<String> response2 = createMockHttpResponse("container-2");

    when(platformClient.createFileContainer()).thenReturn(response1).thenReturn(response2);

    StreamUpdate streamUpdate1 =
        new StreamUpdate(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    StreamUpdate streamUpdate2 =
        new StreamUpdate(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    service.createUploadAndPush(streamUpdate1);
    service.createUploadAndPush(streamUpdate2);

    verify(platformClient, times(2)).createFileContainer();

    ArgumentCaptor<FileContainer> containerCaptor = ArgumentCaptor.forClass(FileContainer.class);
    verify(platformClient, times(2))
        .pushFileContainerContentToStreamSource(eq(SOURCE_ID), containerCaptor.capture());

    assertEquals("container-1", containerCaptor.getAllValues().get(0).fileId);
    assertEquals("container-2", containerCaptor.getAllValues().get(1).fileId);
  }

  @Test
  public void createUploadAndPushShouldPerformOperationsInCorrectOrder()
      throws IOException, InterruptedException {
    StreamUpdate streamUpdate =
        new StreamUpdate(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    service.createUploadAndPush(streamUpdate);

    org.mockito.InOrder inOrder = org.mockito.Mockito.inOrder(platformClient);
    inOrder.verify(platformClient).createFileContainer();
    inOrder
        .verify(platformClient)
        .uploadContentToFileContainer(any(FileContainer.class), any(String.class));
    inOrder
        .verify(platformClient)
        .pushFileContainerContentToStreamSource(eq(SOURCE_ID), any(FileContainer.class));
  }

  @Test
  public void closeOnEmptyQueueShouldReturnNull()
      throws IOException, InterruptedException, NoOpenFileContainerException {
    when(queue.isEmpty()).thenReturn(true);

    HttpResponse<String> result = service.close();

    assertEquals(null, result);
    verify(queue, times(0)).flushAndPush();
  }

  @Test
  public void closeOnNonEmptyQueueShouldReturnFlushAndPushResponse()
      throws IOException, InterruptedException, NoOpenFileContainerException {
    when(queue.isEmpty()).thenReturn(false);
    when(queue.flushAndPush()).thenReturn(httpResponse);

    HttpResponse<String> result = service.close();

    assertEquals(httpResponse, result);
  }

  @Test
  public void serviceShouldSetItselfOnQueueDuringConstruction() {
    verify(queue, times(1)).setUpdateStreamService(service);
  }

  @SuppressWarnings("unchecked")
  private HttpResponse<String> createMockHttpResponse(String fileId) {
    HttpResponse<String> response = mock(HttpResponse.class);
    doReturn(
            String.format(
                "{\"uploadUri\": \"https://upload.uri/%s\", \"fileId\": \"%s\"}", fileId, fileId))
        .when(response)
        .body();
    return response;
  }
}
