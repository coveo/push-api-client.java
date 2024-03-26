package com.coveo.pushapiclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StreamDocumentUploadQueueTest {

  @Mock private UploadStrategy uploadStrategy;

  @InjectMocks private StreamDocumentUploadQueue queue;

  private AutoCloseable closeable;
  private DocumentBuilder documentToAdd;
  private DeleteDocument documentToDelete;
  private PartialUpdateDocument partialUpdateDocument;

  private int oneMegaByte = 1 * 1024 * 1024;

  private String generateStringFromBytes(int numBytes) {
    // Check if the number of bytes is valid
    if (numBytes <= 0) {
      return "";
    }

    // Create a byte array with the specified length
    byte[] bytes = new byte[numBytes];

    // Fill the byte array with a pattern of ASCII characters
    byte pattern = 65; // ASCII value for 'A'
    for (int i = 0; i < numBytes; i++) {
      bytes[i] = pattern;
    }

    return new String(bytes);
  }

  private DocumentBuilder generateDocumentFromSize(int numBytes) {
    return new DocumentBuilder("https://my.document.uri?ref=1", "My bulky document")
        .withData(generateStringFromBytes(numBytes));
  }

  private PartialUpdateDocument generatePartialUpdateDocumentFromSize(int numBytes) {
    return new PartialUpdateDocument(
        "https://my.document.uri?ref=1",
        PartialUpdateOperator.FIELDVALUEREPLACE,
        "field",
        generateStringFromBytes(numBytes));
  }

  @Before
  public void setup() {
    String twoMegaByteData = generateStringFromBytes(2 * oneMegaByte);

    documentToAdd =
        new DocumentBuilder("https://my.document.uri?ref=1", "My new document")
            .withData(twoMegaByteData);

    documentToDelete = new DeleteDocument("https://my.document.uri?ref=3");

    partialUpdateDocument =
        new PartialUpdateDocument(
            "https://my.document.uri?ref=4",
            PartialUpdateOperator.FIELDVALUEREPLACE,
            "field",
            "value");

    closeable = MockitoAnnotations.openMocks(this);
  }

  @After
  public void closeService() throws Exception {
    closeable.close();
  }

  @Test
  public void testIsEmpty() throws IOException, InterruptedException {
    assertTrue(queue.isEmpty());
  }

  @Test
  public void testIsNotEmpty() throws IOException, InterruptedException {
    queue.add(documentToAdd);
    assertFalse(queue.isEmpty());
  }

  @Test
  public void testShouldReturnBatch() throws IOException, InterruptedException {
    StreamUpdate batchUpdate =
        new StreamUpdate(
            new ArrayList<>() {
              {
                add(documentToAdd);
              }
            },
            new ArrayList<>() {
              {
                add(documentToDelete);
              }
            },
            new ArrayList<>() {
              {
                add(partialUpdateDocument);
              }
            });
    queue.add(documentToAdd);
    queue.add(documentToDelete);
    queue.add(partialUpdateDocument);

    assertEquals(batchUpdate, queue.getStream());
  }

  @Test
  public void testFlushShouldNotUploadDocumentsWhenRequiredSizeIsNotMet()
      throws IOException, InterruptedException {
    // Adding 2MB document to the queue => queue has now 3MB of free space
    // (5MB - 2MB = 3MB)
    queue.add(documentToAdd);
    // Adding 2MB document to the queue => queue has now 1MB of free space
    // (3MB - 2MB = 1MB)
    queue.add(documentToDelete);

    // The maximum queue size has not been reached yet (1MB left of free space).
    // Therefore, the accumulated documents will not be automatically flushed.
    // Unless the user runs `.flush()` the queue will keep the 4MB of documents
    verify(uploadStrategy, times(0)).apply(any(BatchUpdate.class));
  }

  @Test
  public void testShouldAutomaticallyFlushAccumulatedDocuments()
      throws IOException, InterruptedException {
    DocumentBuilder firstBulkyDocument = generateDocumentFromSize(2 * oneMegaByte);
    PartialUpdateDocument secondBulkyDocument =
        generatePartialUpdateDocumentFromSize(2 * oneMegaByte);
    DocumentBuilder thirdBulkyDocument = generateDocumentFromSize(2 * oneMegaByte);
    ArrayList<DeleteDocument> emptyList = new ArrayList<>();
    StreamUpdate firstBatch =
        new StreamUpdate(
            new ArrayList<>() {
              {
                add(firstBulkyDocument);
              }
            },
            emptyList,
            new ArrayList<>() {
              {
                add(secondBulkyDocument);
              }
            });

    // Adding 3 documents of 2MB to the queue. After adding the first 2 documents,
    // the queue size will reach 6MB, which exceeds the maximum queue size
    // limit by 1MB. Therefore, the 2 first added documents will automatically be
    // uploaded to the source.
    queue.add(firstBulkyDocument);
    queue.add(secondBulkyDocument);
    verify(uploadStrategy, times(0)).apply(any(BatchUpdate.class));

    // The 3rd document added to the queue will be included in a separate batch,
    // which will not be uploaded unless the `flush()` method is called or until the
    // queue size limit has been reached
    queue.add(thirdBulkyDocument);

    verify(uploadStrategy, times(1)).apply(any(BatchUpdate.class));
    verify(uploadStrategy, times(1)).apply(firstBatch);
  }

  @Test
  public void testShouldManuallyFlushAccumulatedDocuments()
      throws IOException, InterruptedException {
    DocumentBuilder firstBulkyDocument = generateDocumentFromSize(2 * oneMegaByte);
    PartialUpdateDocument secondBulkyDocument =
        generatePartialUpdateDocumentFromSize(2 * oneMegaByte);
    DocumentBuilder thirdBulkyDocument = generateDocumentFromSize(2 * oneMegaByte);
    ArrayList<DeleteDocument> emptyList = new ArrayList<>();
    ArrayList<PartialUpdateDocument> partialEmptyList = new ArrayList<>();
    StreamUpdate firstBatch =
        new StreamUpdate(
            new ArrayList<>() {
              {
                add(firstBulkyDocument);
              }
            },
            emptyList,
            new ArrayList<>() {
              {
                add(secondBulkyDocument);
              }
            });

    StreamUpdate secondBatch =
        new StreamUpdate(
            new ArrayList<>() {
              {
                add(thirdBulkyDocument);
              }
            },
            emptyList,
            partialEmptyList);

    // Adding 3 documents of 2MB to the queue. After adding the first 2 documents,
    // the queue size will reach 6MB, which exceeds the maximum queue size
    // limit. Therefore, the 2 first added documents will automatically be uploaded
    // to the source.
    queue.add(firstBulkyDocument);
    queue.add(secondBulkyDocument);
    queue.add(thirdBulkyDocument);

    queue.flush();

    // Additional flush will have no effect if documents where already flushed
    queue.flush();

    verify(uploadStrategy, times(2)).apply(any(StreamUpdate.class));
    verify(uploadStrategy, times(1)).apply(firstBatch);
    verify(uploadStrategy, times(1)).apply(secondBatch);
  }

  @Test
  public void testAddingEmptyDocument() throws IOException, InterruptedException {
    DocumentBuilder nullDocument = null;

    queue.add(nullDocument);
    queue.flush();

    verify(uploadStrategy, times(0)).apply(any(StreamUpdate.class));
  }

  @Rule public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void getBatchShouldThrowUnsupportedOperationException() {
    expectedException.expect(UnsupportedOperationException.class);
    queue.getBatch();
  }
}
