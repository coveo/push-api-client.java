package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Represents a queue for uploading documents using a specified upload strategy */
class DocumentUploadQueue {
  private static final Logger logger = LogManager.getLogger(DocumentUploadQueue.class);
  private final UploadStrategy uploader;
  private final int maxQueueSize = 5 * 1024 * 1024;
  private ArrayList<DocumentBuilder> documentToAddList;
  private ArrayList<DeleteDocument> documentToDeleteList;
  private int size;

  /**
   * Constructs a new DocumentUploadQueue object with a default maximum queue size limit of 5MB.
   *
   * @param uploader The upload strategy to be used for document uploads.
   */
  public DocumentUploadQueue(UploadStrategy uploader) {
    this.documentToAddList = new ArrayList<>();
    this.documentToDeleteList = new ArrayList<>();
    this.uploader = uploader;
  }

  /**
   * Flushes the accumulated documents by applying the upload strategy.
   *
   * @throws IOException If an I/O error occurs during the upload.
   * @throws InterruptedException If the upload process is interrupted.
   */
  public void flush() throws IOException, InterruptedException {
    if (this.isEmpty()) {
      logger.debug("Empty batch. Skipping upload");
      return;
    }
    // TODO: LENS-871: support concurrent requests
    this.applyStrategy();

    this.size = 0;
    this.documentToAddList.clear();
    this.documentToDeleteList.clear();
  }

  private void applyStrategy() throws IOException, InterruptedException {
    BatchUpdate batch = this.getBatch();
    logger.info("Uploading document batch");
    HttpResponse<String> response = this.uploader.apply(batch);
    if (response != null && !response.body().isEmpty()) {
      logger.info("Document batch upload response: " + response.body());
    }
  }

  /**
   * Adds a {@link DocumentBuilder} to the upload queue and flushes the queue if it exceeds the
   * maximum content length. See {@link DocumentUploadQueue#flush}.
   *
   * @param document The document to be added to the index.
   * @throws IOException If an I/O error occurs during the upload.
   * @throws InterruptedException If the upload process is interrupted.
   */
  public void add(DocumentBuilder document) throws IOException, InterruptedException {
    if (document == null) {
      return;
    }

    final int sizeOfDoc = document.marshal().getBytes().length;
    if (this.size + sizeOfDoc >= this.maxQueueSize) {
      this.flush();
    }
    documentToAddList.add(document);
    logger.info("Adding document to batch: " + document.getDocument().uri);
    this.size += sizeOfDoc;
  }

  /**
   * Adds the {@link DeleteDocument} to the upload queue and flushes the queue if it exceeds the
   * maximum content length. See {@link DocumentUploadQueue#flush}.
   *
   * @param document The document to be deleted from the index.
   * @throws IOException If an I/O error occurs during the upload.
   * @throws InterruptedException If the upload process is interrupted.
   */
  public void add(DeleteDocument document) throws IOException, InterruptedException {
    if (document == null) {
      return;
    }

    final int sizeOfDoc = document.marshalJsonObject().toString().getBytes().length;
    if (this.size + sizeOfDoc >= this.maxQueueSize) {
      this.flush();
    }
    documentToDeleteList.add(document);
    logger.info("Adding document to batch: " + document.documentId);
    this.size += sizeOfDoc;
  }

  public BatchUpdate getBatch() {
    return new BatchUpdate(
        new ArrayList<DocumentBuilder>(this.documentToAddList),
        new ArrayList<DeleteDocument>(this.documentToDeleteList));
  }

  public boolean isEmpty() {
    // TODO: LENS-843: include partial document updates
    return documentToAddList.isEmpty() && documentToDeleteList.isEmpty();
  }
}
