package com.coveo.pushapiclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class StreamDocumentUploadQueueTest {

  private static final int TEST_BATCH_SIZE = 5 * 1024 * 1024;

  @Mock private UploadStrategy uploadStrategy;
  @Mock private UpdateStreamServiceInternal updateStreamService;
  @Mock private HttpResponse<String> httpResponse;

  private StreamDocumentUploadQueue queue;
  private AutoCloseable closeable;
  private DocumentBuilder documentToAdd;
  private DeleteDocument documentToDelete;
  private PartialUpdateDocument partialUpdateDocument;

  private int oneMegaByte = 1 * 1024 * 1024;

  private String generateStringFromBytes(int numBytes) {
    if (numBytes <= 0) {
      return "";
    }
    byte[] bytes = new byte[numBytes];
    byte pattern = 65;
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
  public void setup() throws IOException, InterruptedException {
    closeable = MockitoAnnotations.openMocks(this);

    queue = new StreamDocumentUploadQueue(uploadStrategy, TEST_BATCH_SIZE);
    queue.setUpdateStreamService(updateStreamService);

    when(updateStreamService.createUploadAndPush(any(StreamUpdate.class))).thenReturn(httpResponse);

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
    queue.add(documentToAdd);
    queue.add(documentToDelete);

    verify(updateStreamService, times(0)).createUploadAndPush(any(StreamUpdate.class));
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

    queue.add(firstBulkyDocument);
    queue.add(secondBulkyDocument);
    verify(updateStreamService, times(0)).createUploadAndPush(any(StreamUpdate.class));

    queue.add(thirdBulkyDocument);

    verify(updateStreamService, times(1)).createUploadAndPush(any(StreamUpdate.class));
    verify(updateStreamService, times(1)).createUploadAndPush(firstBatch);
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

    queue.add(firstBulkyDocument);
    queue.add(secondBulkyDocument);
    queue.add(thirdBulkyDocument);

    queue.flush();

    queue.flush();

    verify(updateStreamService, times(1)).createUploadAndPush(firstBatch);
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
