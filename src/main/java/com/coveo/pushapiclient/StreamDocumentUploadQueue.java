package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StreamDocumentUploadQueue extends DocumentUploadQueue<StreamUpdate> {

  private static final Logger logger = LogManager.getLogger(StreamDocumentUploadQueue.class);
  protected ArrayList<PartialUpdateDocument> documentToPartiallyUpdateList;

  /**
   * Creates a StreamDocumentUploadQueue configured for UpdateStreamService operations.
   * This factory method provides proper initialization without requiring post-construction setup.
   *
   * @param source The stream-enabled source for the upload operations
   * @param platformClient The platform client for API calls
   * @param maxQueueSize The maximum queue size in bytes
   * @return A fully configured StreamDocumentUploadQueue
   */
  public static StreamDocumentUploadQueue forStreamSource(
      StreamEnabledSource source,
      PlatformClient platformClient,
      int maxQueueSize) {
    StreamUploadStrategy strategy = new StreamUploadStrategy(source, platformClient);
    return new StreamDocumentUploadQueue(strategy, maxQueueSize);
  }

  public StreamDocumentUploadQueue(UploadStrategy<StreamUpdate> uploader) {
    super(uploader);
    this.documentToPartiallyUpdateList = new ArrayList<>();
  }

  /**
   * Constructs a new StreamDocumentUploadQueue object with a configurable maximum queue size limit.
   *
   * @param uploader The upload strategy to be used for document uploads.
   * @param maxQueueSize The maximum queue size in bytes. Must not exceed 256MB (Stream API limit).
   * @throws IllegalArgumentException if maxQueueSize exceeds the API limit of 256MB.
   */
  public StreamDocumentUploadQueue(UploadStrategy<StreamUpdate> uploader, int maxQueueSize) {
    super(uploader, maxQueueSize);
    this.documentToPartiallyUpdateList = new ArrayList<>();
  }

  /**
   * @deprecated Use the factory method {@link #forStreamSource} instead.
   * This method will be removed in a future version.
   */
  @Deprecated
  public void setUpdateStreamService(UpdateStreamServiceInternal updateStreamService) {
    // No-op for backward compatibility
    // The strategy pattern now handles this
  }

  private void clearQueue() {
    this.size = 0;
    this.documentToAddList.clear();
    this.documentToDeleteList.clear();
    this.documentToPartiallyUpdateList.clear();
  }

  /**
   * Flushes the accumulated documents by applying the upload strategy.
   *
   * <p>Note: This method is deprecated for catalog stream updates. Use flushAndPush() instead,
   * which properly handles the create-upload-push workflow for each file container.
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

    if (this.uploader == null) {
      throw new IllegalStateException(
          "No upload strategy configured. Use StreamDocumentUploadQueue.forStreamSource() to create a properly configured queue.");
    }

    StreamUpdate stream = this.getStream();
    logger.info("Uploading document Stream");
    this.uploader.apply(stream);
    clearQueue();
  }

  /**
   * Flushes the accumulated documents and pushes them to the stream source.
   * Each flush creates a new file container, uploads to it, and pushes it.
   *
   * @return The HTTP response from the push operation, or null if queue was empty
   * @throws IOException If an I/O error occurs during the upload.
   * @throws InterruptedException If the upload process is interrupted.
   */
  public HttpResponse<String> flushAndPush() throws IOException, InterruptedException {
    if (this.isEmpty()) {
      logger.debug("Empty batch. Skipping upload");
      return null;
    }

    if (this.uploader == null) {
      throw new IllegalStateException(
          "No upload strategy configured. Use StreamDocumentUploadQueue.forStreamSource() to create a properly configured queue.");
    }

    StreamUpdate stream = this.getStream();
    logger.info("Flushing and pushing stream batch");
    HttpResponse<String> response = this.uploader.apply(stream);
    clearQueue();
    return response;
  }

  /**
   * Adds the {@link PartialUpdateDocument} to the upload queue and flushes the queue if it exceeds
   * the maximum content length. Each flush creates a new file container, uploads to it, and pushes
   * it to the stream source.
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
      this.flushAndPush();
    }
    documentToPartiallyUpdateList.add(document);
    if (logger.isDebugEnabled()) {
      logger.debug("Adding document to batch: " + document.documentId);
    }
    this.size += sizeOfDoc;
  }

  /**
   * Adds a {@link DocumentBuilder} to the upload queue and flushes the queue if it exceeds the
   * maximum content length. Each flush creates a new file container, uploads to it, and pushes it
   * to the stream source.
   *
   * @param document The document to be added to the index.
   * @throws IOException If an I/O error occurs during the upload.
   * @throws InterruptedException If the upload process is interrupted.
   */
  @Override
  public void add(DocumentBuilder document) throws IOException, InterruptedException {
    if (document == null) {
      return;
    }

    final int sizeOfDoc = document.marshal().getBytes().length;
    if (this.size + sizeOfDoc >= this.maxQueueSize) {
      this.flushAndPush();
    }
    documentToAddList.add(document);
    if (logger.isDebugEnabled()) {
      logger.debug("Adding document to batch: " + document.getDocument().uri);
    }
    this.size += sizeOfDoc;
  }

  /**
   * Adds the {@link DeleteDocument} to the upload queue and flushes the queue if it exceeds the
   * maximum content length. Each flush creates a new file container, uploads to it, and pushes it
   * to the stream source.
   *
   * @param document The document to be deleted from the index.
   * @throws IOException If an I/O error occurs during the upload.
   * @throws InterruptedException If the upload process is interrupted.
   */
  @Override
  public void add(DeleteDocument document) throws IOException, InterruptedException {
    if (document == null) {
      return;
    }

    final int sizeOfDoc = document.marshalJsonObject().toString().getBytes().length;
    if (this.size + sizeOfDoc >= this.maxQueueSize) {
      this.flushAndPush();
    }
    documentToDeleteList.add(document);
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
