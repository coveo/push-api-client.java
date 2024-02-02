package com.coveo.pushapiclient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coveo.pushapiclient.exceptions.NoOpenFileContainerException;
import com.coveo.pushapiclient.exceptions.NoOpenStreamException;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.apache.logging.log4j.core.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class StreamServiceInternalTest {
  @Mock private StreamEnabledSource source;

  @Mock private DocumentUploadQueue queue;

  @Mock private PlatformClient platformClient;

  @Mock private Logger logger;

  @Spy @InjectMocks private StreamServiceInternal service;

  @Mock private HttpResponse<String> httpResponse;

  @Mock private HttpResponse<String> fileContainerHttpResponse;

  private AutoCloseable closeable;
  private DocumentBuilder documentA;
  private DocumentBuilder documentB;
  private DeleteDocument deleteDocumentA;

  @Before
  public void setUp() throws Exception {
    documentA = new DocumentBuilder("https://my.document.uri?ref=1", "My first document title");
    documentB = new DocumentBuilder("https://my.document.uri?ref=2", "My second document title");
    deleteDocumentA = new DeleteDocument("https://my.document.uri?ref=1");

    closeable = MockitoAnnotations.openMocks(this);

    when(httpResponse.body()).thenReturn("{\"streamId\": \"stream-id\"}");
    when(platformClient.openStream("my-source-id")).thenReturn(httpResponse);
    when(fileContainerHttpResponse.body())
        .thenReturn("{\"uploadUri\": \"https://upload.uri\", \"fileId\": \"file-id\"}");
    when(platformClient.createFileContainer()).thenReturn(fileContainerHttpResponse);
    when(source.getId()).thenReturn("my-source-id");
  }

  @After
  public void closeService() throws Exception {
    closeable.close();
  }

  @Test
  public void testAddShouldOpenANewStream() throws IOException, InterruptedException {
    service.add(documentA);
    service.add(documentB);

    verify(this.platformClient, times(1)).openStream("my-source-id");
  }

  @Test
  public void testAddShouldAddDocumentToQueue() throws IOException, InterruptedException {
    service.add(documentA);
    service.add(documentB);

    verify(queue, times(1)).add(documentA);
    verify(queue, times(1)).add(documentB);
  }

  @Test
  public void testCloseShouldCloseOpenStream()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    doReturn(StreamOperationType.REBUILD).when(service).getOperationType();
    service.add(documentA);
    service.close();

    verify(platformClient, times(1)).closeStream("my-source-id", "stream-id");
  }

  @Test
  public void testCloseShouldFlushBufferedDocuments()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    doReturn(StreamOperationType.REBUILD).when(service).getOperationType();
    service.add(documentA);
    service.close();

    verify(queue, times(1)).flush();
  }

  @Test(expected = NoOpenStreamException.class)
  public void givenNoOpenStream_whenClose_thenShouldThrow()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    doReturn(StreamOperationType.REBUILD).when(service).getOperationType();
    service.close();
  }

  @Test
  public void testShouldLogInfo()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    doReturn(StreamOperationType.REBUILD).when(service).getOperationType();
    service.add(documentA);
    service.add(documentB);
    verify(logger, times(1)).info("Opening new stream");

    service.close();
    verify(logger, times(1)).info(contains("Closing open stream"));
  }

  @Test
  public void testPushFileContainer_ShouldPushFileContainer_OnAddOrUpdate()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    doReturn(StreamOperationType.INCREMENTAL).when(service).getOperationType();
    service.addOrUpdate(documentA);
    service.close();

    verify(platformClient, times(1))
        .pushFileContainerContentToStreamSource(eq("my-source-id"), any(FileContainer.class));
  }

  @Test
  public void testPushFileContainer_ShouldPushFileContainer_OnDelete()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    doReturn(StreamOperationType.INCREMENTAL).when(service).getOperationType();
    service.delete(deleteDocumentA);
    service.close();

    verify(platformClient, times(1))
        .pushFileContainerContentToStreamSource(eq("my-source-id"), any(FileContainer.class));
  }

  @Test
  public void testCloseShouldFlushBufferedDocuments_OnIncrementOperation()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    doReturn(StreamOperationType.INCREMENTAL).when(service).getOperationType();
    service.addOrUpdate(documentA);
    service.close();

    verify(queue, times(1)).flush();
  }

  @Test(expected = NoOpenFileContainerException.class)
  public void givenNoFileContainer_whenPush_thenShouldThrow()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    doReturn(StreamOperationType.INCREMENTAL).when(service).getOperationType();
    service.close();
  }

  @Test
  public void testShouldLogInfo_OnPushContainer()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    doReturn(StreamOperationType.INCREMENTAL).when(service).getOperationType();
    service.addOrUpdate(documentA);
    verify(logger, times(1)).info("Creating new file container");
    service.close();
    verify(logger, times(1)).info("Pushing to file container file-id");
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testShouldThrowException_OnAddOrUpdate_WhenRebuild()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    doReturn(StreamOperationType.REBUILD).when(service).getOperationType();
    service.addOrUpdate(documentA);
    service.close();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testShouldThrowException_OnDelete_WhenRebuild()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    doReturn(StreamOperationType.REBUILD).when(service).getOperationType();
    service.delete(deleteDocumentA);
    service.close();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testShouldThrowException_OnAdd_WhenRebuild()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    doReturn(StreamOperationType.INCREMENTAL).when(service).getOperationType();
    service.add(documentA);
    service.close();
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testShouldThrowException_OnClose_WhenNoOperationType()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    service.close();
  }
}
