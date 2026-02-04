package com.coveo.pushapiclient;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coveo.pushapiclient.exceptions.NoOpenFileContainerException;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.apache.logging.log4j.core.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
  public void addOrUpdateShouldAddDocumentsToQueue() throws IOException, InterruptedException {
    service.addOrUpdate(documentA);
    service.addOrUpdate(documentB);

    verify(queue, times(1)).add(documentA);
    verify(queue, times(1)).add(documentB);
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
  public void deleteShouldAddDocumentsToQueue() throws IOException, InterruptedException {
    service.delete(deleteDocumentA);
    service.delete(deleteDocumentB);

    verify(queue, times(1)).add(deleteDocumentA);
    verify(queue, times(1)).add(deleteDocumentB);
  }

  @Test
  public void partialUpdateShouldAddDocumentsToQueue() throws IOException, InterruptedException {
    service.addPartialUpdate(partialUpdateDocumentA);
    service.addPartialUpdate(partialUpdateDocumentB);

    verify(queue, times(1)).add(partialUpdateDocumentA);
    verify(queue, times(1)).add(partialUpdateDocumentB);
  }

  @Test
  public void closeShouldCallFlushAndPush()
      throws IOException, InterruptedException, NoOpenFileContainerException {
    service.addOrUpdate(documentA);
    service.close();

    verify(queue, times(1)).flush();
  }

  @Test
  public void closeShouldReturnFlushAndPushResponse()
      throws IOException, InterruptedException, NoOpenFileContainerException {
    when(queue.getLastResponse()).thenReturn(httpResponse);
    service.addOrUpdate(documentA);
    
    HttpResponse<String> result = service.close();

    assertEquals(httpResponse, result);
  }

  @Test
  public void closeShouldReturnNullWhenQueueIsEmpty()
      throws IOException, InterruptedException, NoOpenFileContainerException {
    when(queue.getLastResponse()).thenReturn(null);

    HttpResponse<String> result = service.close();

    assertEquals(null, result);
  }
}
