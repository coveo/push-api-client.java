package com.coveo.pushapiclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Tests for container rotation and batching behavior in the stream update workflow. Each batch that
 * exceeds the configured limit should trigger creation of a new file container, upload, and
 * immediate push.
 */
public class StreamDocumentUploadQueueBatchingTest {

  private static final int SMALL_BATCH_SIZE = 5000;

  @Mock private UpdateStreamServiceInternal updateStreamService;
  @Mock private HttpResponse<String> httpResponse;

  private StreamDocumentUploadQueue queue;
  private AutoCloseable closeable;

  @Before
  public void setUp() throws Exception {
    closeable = MockitoAnnotations.openMocks(this);
    queue = new StreamDocumentUploadQueue(null, SMALL_BATCH_SIZE);
    queue.setUpdateStreamService(updateStreamService);

    when(updateStreamService.createUploadAndPush(any(StreamUpdate.class))).thenReturn(httpResponse);
  }

  @After
  public void tearDown() throws Exception {
    closeable.close();
  }

  @Test
  public void addingDocumentsThatExceedBatchSizeShouldTriggerFlushAndPush()
      throws IOException, InterruptedException {
    DocumentBuilder doc1 =
        new DocumentBuilder("https://doc.uri/1", "Doc 1").withData(generateData(3000));
    DocumentBuilder doc2 =
        new DocumentBuilder("https://doc.uri/2", "Doc 2").withData(generateData(3000));

    queue.add(doc1);
    verify(updateStreamService, times(0)).createUploadAndPush(any(StreamUpdate.class));

    queue.add(doc2);
    verify(updateStreamService, times(1)).createUploadAndPush(any(StreamUpdate.class));
  }

  @Test
  public void addMultipleSmallDocumentsShouldNotTriggerFlushUntilLimitReached()
      throws IOException, InterruptedException {
    DocumentBuilder smallDoc1 = new DocumentBuilder("https://doc.uri/1", "Small Doc 1");
    DocumentBuilder smallDoc2 = new DocumentBuilder("https://doc.uri/2", "Small Doc 2");

    queue.add(smallDoc1);
    queue.add(smallDoc2);

    verify(updateStreamService, times(0)).createUploadAndPush(any(StreamUpdate.class));
    assertFalse(queue.isEmpty());
  }

  @Test
  public void accumulatedDocumentsExceedingLimitShouldFlushPreviousBatch()
      throws IOException, InterruptedException {
    DocumentBuilder doc1 =
        new DocumentBuilder("https://doc.uri/1", "Doc 1").withData(generateData(2000));
    DocumentBuilder doc2 =
        new DocumentBuilder("https://doc.uri/2", "Doc 2").withData(generateData(2000));
    DocumentBuilder doc3 =
        new DocumentBuilder("https://doc.uri/3", "Doc 3").withData(generateData(2000));

    queue.add(doc1);
    queue.add(doc2);
    verify(updateStreamService, times(0)).createUploadAndPush(any(StreamUpdate.class));

    queue.add(doc3);
    verify(updateStreamService, times(1)).createUploadAndPush(any(StreamUpdate.class));

    ArgumentCaptor<StreamUpdate> captor = ArgumentCaptor.forClass(StreamUpdate.class);
    verify(updateStreamService).createUploadAndPush(captor.capture());
    assertEquals(2, captor.getValue().getAddOrUpdate().size());
  }

  @Test
  public void multipleBatchesShouldCreateMultipleContainers()
      throws IOException, InterruptedException {
    DocumentBuilder doc1 =
        new DocumentBuilder("https://doc.uri/1", "Doc 1").withData(generateData(3000));
    DocumentBuilder doc2 =
        new DocumentBuilder("https://doc.uri/2", "Doc 2").withData(generateData(3000));
    DocumentBuilder doc3 =
        new DocumentBuilder("https://doc.uri/3", "Doc 3").withData(generateData(3000));
    DocumentBuilder doc4 =
        new DocumentBuilder("https://doc.uri/4", "Doc 4").withData(generateData(3000));

    queue.add(doc1);
    queue.add(doc2);
    queue.add(doc3);
    queue.add(doc4);

    verify(updateStreamService, times(3)).createUploadAndPush(any(StreamUpdate.class));
  }

  @Test
  public void flushAndPushShouldClearQueueAfterBatch() throws IOException, InterruptedException {
    DocumentBuilder doc =
        new DocumentBuilder("https://doc.uri/1", "Doc").withData(generateData(10));
    queue.add(doc);
    assertFalse(queue.isEmpty());

    queue.flushAndPush();

    assertTrue(queue.isEmpty());
  }

  @Test
  public void flushAndPushShouldReturnResponseFromService()
      throws IOException, InterruptedException {
    DocumentBuilder doc =
        new DocumentBuilder("https://doc.uri/1", "Doc").withData(generateData(10));
    queue.add(doc);

    HttpResponse<String> response = queue.flushAndPush();

    assertEquals(httpResponse, response);
  }

  @Test
  public void flushAndPushOnEmptyQueueShouldReturnNull() throws IOException, InterruptedException {
    HttpResponse<String> response = queue.flushAndPush();

    assertNull(response);
    verify(updateStreamService, times(0)).createUploadAndPush(any(StreamUpdate.class));
  }

  @Test
  public void flushAndPushShouldPassCorrectStreamUpdateToService()
      throws IOException, InterruptedException {
    DocumentBuilder doc = new DocumentBuilder("https://doc.uri/1", "Doc");
    DeleteDocument deleteDoc = new DeleteDocument("https://doc.uri/2");
    PartialUpdateDocument partialDoc =
        new PartialUpdateDocument(
            "https://doc.uri/3", PartialUpdateOperator.FIELDVALUEREPLACE, "field", "value");

    queue.add(doc);
    queue.add(deleteDoc);
    queue.add(partialDoc);

    queue.flushAndPush();

    ArgumentCaptor<StreamUpdate> captor = ArgumentCaptor.forClass(StreamUpdate.class);
    verify(updateStreamService).createUploadAndPush(captor.capture());

    StreamUpdate captured = captor.getValue();
    assertEquals(1, captured.getAddOrUpdate().size());
    assertEquals(1, captured.getDelete().size());
    assertEquals(1, captured.getPartialUpdate().size());
  }

  @Test
  public void deleteDocumentsTriggerFlushWhenExceedingLimit()
      throws IOException, InterruptedException {
    queue = new StreamDocumentUploadQueue(null, 50);
    queue.setUpdateStreamService(updateStreamService);

    DeleteDocument deleteDoc1 = new DeleteDocument("https://doc.uri/1");
    DeleteDocument deleteDoc2 =
        new DeleteDocument("https://doc.uri/with/very/long/path/that/exceeds");

    queue.add(deleteDoc1);
    verify(updateStreamService, times(0)).createUploadAndPush(any(StreamUpdate.class));

    queue.add(deleteDoc2);
    verify(updateStreamService, times(1)).createUploadAndPush(any(StreamUpdate.class));
  }

  @Test
  public void partialUpdateDocumentsTriggerFlushWhenExceedingLimit()
      throws IOException, InterruptedException {
    PartialUpdateDocument partialDoc1 =
        new PartialUpdateDocument(
            "https://doc.uri/1", PartialUpdateOperator.FIELDVALUEREPLACE, "f", "v");
    PartialUpdateDocument partialDoc2 =
        new PartialUpdateDocument(
            "https://doc.uri/2",
            PartialUpdateOperator.FIELDVALUEREPLACE,
            "field",
            generateData(SMALL_BATCH_SIZE));

    queue.add(partialDoc1);
    verify(updateStreamService, times(0)).createUploadAndPush(any(StreamUpdate.class));

    queue.add(partialDoc2);
    verify(updateStreamService, times(1)).createUploadAndPush(any(StreamUpdate.class));
  }

  @Test
  public void mixedDocumentTypesShouldAccumulateAndFlushCorrectly()
      throws IOException, InterruptedException {
    DocumentBuilder doc =
        new DocumentBuilder("https://doc.uri/1", "Doc").withData(generateData(1500));
    DeleteDocument deleteDoc = new DeleteDocument("https://doc.uri/2");
    PartialUpdateDocument partialDoc =
        new PartialUpdateDocument(
            "https://doc.uri/3",
            PartialUpdateOperator.FIELDVALUEREPLACE,
            "field",
            generateData(4000));

    queue.add(doc);
    queue.add(deleteDoc);
    verify(updateStreamService, times(0)).createUploadAndPush(any(StreamUpdate.class));

    queue.add(partialDoc);
    verify(updateStreamService, times(1)).createUploadAndPush(any(StreamUpdate.class));
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorShouldRejectBatchSizeExceeding256MB() {
    int exceeding256MB = 256 * 1024 * 1024 + 1;
    new StreamDocumentUploadQueue(null, exceeding256MB);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorShouldRejectZeroBatchSize() {
    new StreamDocumentUploadQueue(null, 0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void constructorShouldRejectNegativeBatchSize() {
    new StreamDocumentUploadQueue(null, -1);
  }

  @Test
  public void constructorShouldAcceptMaxAllowedBatchSize() {
    int max256MB = 256 * 1024 * 1024;
    StreamDocumentUploadQueue q = new StreamDocumentUploadQueue(null, max256MB);
    assertNotNull(q);
  }

  @Test
  public void queueShouldUseSystemPropertyForDefaultBatchSize() {
    String originalValue = System.getProperty(DocumentUploadQueue.BATCH_SIZE_PROPERTY);
    try {
      System.setProperty(DocumentUploadQueue.BATCH_SIZE_PROPERTY, "1048576");
      int configuredSize = DocumentUploadQueue.getConfiguredBatchSize();
      assertEquals(1048576, configuredSize);
    } finally {
      if (originalValue != null) {
        System.setProperty(DocumentUploadQueue.BATCH_SIZE_PROPERTY, originalValue);
      } else {
        System.clearProperty(DocumentUploadQueue.BATCH_SIZE_PROPERTY);
      }
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void systemPropertyExceeding256MBShouldThrow() {
    String originalValue = System.getProperty(DocumentUploadQueue.BATCH_SIZE_PROPERTY);
    try {
      System.setProperty(DocumentUploadQueue.BATCH_SIZE_PROPERTY, "268435457");
      DocumentUploadQueue.getConfiguredBatchSize();
    } finally {
      if (originalValue != null) {
        System.setProperty(DocumentUploadQueue.BATCH_SIZE_PROPERTY, originalValue);
      } else {
        System.clearProperty(DocumentUploadQueue.BATCH_SIZE_PROPERTY);
      }
    }
  }

  private String generateData(int numBytes) {
    if (numBytes <= 0) return "";
    byte[] bytes = new byte[numBytes];
    for (int i = 0; i < numBytes; i++) {
      bytes[i] = 65;
    }
    return new String(bytes);
  }
}
