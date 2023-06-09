package com.coveo.pushapiclient;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.coveo.pushapiclient.exceptions.NoOpenStreamException;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PushServiceInternalTest {
  @Mock private DocumentUploadQueue queue;

  @InjectMocks private PushServiceInternal service;

  @Mock private HttpResponse<String> httpResponse;

  private AutoCloseable closeable;
  private DocumentBuilder documentA;
  private DocumentBuilder documentB;
  private DeleteDocument documentC;

  @Before
  public void setUp() throws Exception {
    documentA = new DocumentBuilder("https://my.document.uri?ref=1", "My first document title");
    documentB = new DocumentBuilder("https://my.document.uri?ref=2", "My second document title");
    documentC = new DeleteDocument("https://my.document.uri?ref=3");

    closeable = MockitoAnnotations.openMocks(this);
  }

  @After
  public void closeService() throws Exception {
    closeable.close();
  }

  @Test
  public void testShouldAddNewDocumentToQueue() throws IOException, InterruptedException {
    service.addOrUpdate(documentA);
    service.addOrUpdate(documentB);

    verify(this.queue, times(1)).add(documentA);
    verify(this.queue, times(1)).add(documentB);
  }

  @Test
  public void testAddShouldAddDocumentToDeleteToQueue() throws IOException, InterruptedException {
    service.delete(documentC);

    verify(queue, times(1)).add(documentC);
  }

  @Test
  public void testCloseShouldFlushBufferedDocuments()
      throws IOException, InterruptedException, NoOpenStreamException {
    service.addOrUpdate(documentA);
    service.addOrUpdate(documentB);
    service.delete(documentC);
    service.close();

    verify(queue, times(1)).flush();
  }
}
