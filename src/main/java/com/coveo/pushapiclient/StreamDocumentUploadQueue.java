package com.coveo.pushapiclient;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StreamDocumentUploadQueue extends DocumentUploadQueue {

  private static final Logger logger = LogManager.getLogger(StreamDocumentUploadQueue.class);
  protected ArrayList<PartialUpdateDocument> documentToPartiallyUpdateList;

  public StreamDocumentUploadQueue(UploadStrategy uploader) {
    super(uploader);
    this.documentToPartiallyUpdateList = new ArrayList<>();
  }

  /**
   * Flushes the accumulated documents by applying the upload strategy.
   *
   * @throws IOException If an I/O error occurs during the upload.
   * @throws InterruptedException If the upload process is interrupted.
   */
  @Override
  public void flush() throws IOException, InterruptedException {
    if (this.isEmpty()) {
      logger.debug("Empty batch. Skipping upload");
      return;
    }
    // TODO: LENS-871: support concurrent requests
    StreamUpdate stream = this.getStream();
    logger.info("Uploading document Stream");
    this.uploader.apply(stream);

    this.size = 0;
    this.documentToAddList.clear();
    this.documentToDeleteList.clear();
    this.documentToPartiallyUpdateList.clear();
  }

  /**
   * Adds the {@link PartialUpdateDocument} to the upload queue and flushes the queue if it exceeds
   * the maximum content length. See {@link PartialUpdateDocument#flush}.
   *
   * @param document The document to be deleted from the index.
   * @throws IOException If an I/O error occurs during the upload.
   * @throws InterruptedException If the upload process is interrupted.
   */
  public void add(PartialUpdateDocument document) throws IOException, InterruptedException {
    if (document == null) {
      return;
    }

    final int sizeOfDoc = document.marshalJsonObject().toString().getBytes().length;
    if (this.size + sizeOfDoc >= this.maxQueueSize) {
      this.flush();
    }
    documentToPartiallyUpdateList.add(document);
    if (logger.isDebugEnabled()) {
      logger.debug("Adding document to batch: " + document.documentId);
    }
    this.size += sizeOfDoc;
  }

  public StreamUpdate getStream() {
    return new StreamUpdate(
        new ArrayList<>(this.documentToAddList),
        new ArrayList<>(this.documentToDeleteList),
        new ArrayList<>(this.documentToPartiallyUpdateList));
  }

  @Override
  public BatchUpdate getBatch() {
    throw new UnsupportedOperationException("StreamDocumentUploadQueue does not support getBatch");
  }

  @Override
  public boolean isEmpty() {
    return super.isEmpty() && documentToPartiallyUpdateList.isEmpty();
  }
}
