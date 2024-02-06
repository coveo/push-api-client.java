package com.coveo.pushapiclient;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

public class StreamServiceInternalTest {
  @Mock private StreamEnabledSource source;

  @Mock private DocumentUploadQueue queue;

  @Mock private PlatformClient platformClient;

  @Mock private Logger logger;

  @InjectMocks private StreamServiceInternal service;

  @Mock private HttpResponse<String> httpResponse;

  private AutoCloseable closeable;
  private DocumentBuilder documentA;
  private DocumentBuilder documentB;

  @Before
  public void setUp() throws Exception {
    documentA = new DocumentBuilder("https://my.document.uri?ref=1", "My first document title");
    documentB = new DocumentBuilder("https://my.document.uri?ref=2", "My second document title");

    closeable = MockitoAnnotations.openMocks(this);

    when(httpResponse.body()).thenReturn("{\"streamId\": \"stream-id\"}");
    when(platformClient.openStream("my-source-id")).thenReturn(httpResponse);
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
      throws IOException, InterruptedException, NoOpenStreamException {
    service.add(documentA);
    service.close();

    verify(platformClient, times(1)).closeStream("my-source-id", "stream-id");
  }

  @Test
  public void testCloseShouldFlushBufferedDocuments()
      throws IOException, InterruptedException, NoOpenStreamException {
    service.add(documentA);
    service.close();

    verify(queue, times(1)).flush();
  }

  @Test(expected = NoOpenStreamException.class)
  public void givenNoOpenStream_whenClose_thenShouldThrow()
      throws IOException, InterruptedException, NoOpenStreamException {
    service.close();
  }

  @Test
  public void testShouldLogInfo() throws IOException, InterruptedException, NoOpenStreamException {
    service.add(documentA);
    service.add(documentB);
    verify(logger, times(1)).info("Opening new stream");

    service.close();
    verify(logger, times(1)).info(contains("Closing open stream"));
  }
}
