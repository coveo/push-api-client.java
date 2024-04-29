package com.coveo.pushapiclient;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Represents a queue for uploading documents using a specified upload strategy */
class DocumentUploadQueue {
  private static final Logger logger = LogManager.getLogger(DocumentUploadQueue.class);
  protected final UploadStrategy uploader;
  protected final int maxQueueSize = 5 * 1024 * 1024;
  protected ArrayList<DocumentBuilder> documentToAddList;
  protected ArrayList<DeleteDocument> documentToDeleteList;
  protected int size;

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
    BatchUpdate batch = this.getBatch();
    logger.info("Uploading document batch");
    this.uploader.apply(batch);

    this.size = 0;
    this.documentToAddList.clear();
    this.documentToDeleteList.clear();
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
    if (logger.isDebugEnabled()) {
      logger.info("Adding document to batch: " + document.getDocument().uri);
    }
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
    if (logger.isDebugEnabled()) {
      logger.debug("Adding document to batch: " + document.documentId);
    }
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
