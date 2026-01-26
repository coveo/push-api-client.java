package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StreamDocumentUploadQueue extends DocumentUploadQueue {

  private static final Logger logger = LogManager.getLogger(StreamDocumentUploadQueue.class);
  protected ArrayList<PartialUpdateDocument> documentToPartiallyUpdateList;
  private UpdateStreamServiceInternal updateStreamService;

  public StreamDocumentUploadQueue(UploadStrategy uploader) {
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
  public StreamDocumentUploadQueue(UploadStrategy uploader, int maxQueueSize) {
    super(uploader, maxQueueSize);
    this.documentToPartiallyUpdateList = new ArrayList<>();
  }
  
  /**
   * Sets the UpdateStreamServiceInternal reference for handling complete upload workflow.
   * This is needed to support the new pattern where each flush creates its own file container.
   * 
   * @param updateStreamService The service that handles create/upload/push operations
   */
  public void setUpdateStreamService(UpdateStreamServiceInternal updateStreamService) {
    this.updateStreamService = updateStreamService;
  }

  /**
   * Flushes the accumulated documents by applying the upload strategy.
   * 
   * Note: This method is deprecated for catalog stream updates. Use flushAndPush() instead,
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
          "No upload strategy configured. For UpdateStreamService, use flushAndPush() instead.");
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
   * Flushes the accumulated documents and pushes them to the stream source.
   * This method implements the proper workflow for catalog stream API updates:
   * 1. Create a new file container
   * 2. Upload content to the container
   * 3. Push the container to the stream source
   * 
   * Each flush operation gets its own file container, as required by the catalog stream API.
   *
   * @return The HTTP response from the push operation
   * @throws IOException If an I/O error occurs during the upload.
   * @throws InterruptedException If the upload process is interrupted.
   */
  public HttpResponse<String> flushAndPush() throws IOException, InterruptedException {
    if (this.isEmpty()) {
      logger.debug("Empty batch. Skipping upload");
      return null;
    }
    
    StreamUpdate stream = this.getStream();
    logger.info("Creating file container, uploading, and pushing stream batch");
    
    // Use the new createUploadAndPush method that handles the complete workflow
    HttpResponse<String> response = this.updateStreamService.createUploadAndPush(stream);

    this.size = 0;
    this.documentToAddList.clear();
    this.documentToDeleteList.clear();
    this.documentToPartiallyUpdateList.clear();
    
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
   * maximum content length. Each flush creates a new file container, uploads to it, and pushes 
   * it to the stream source.
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
   * maximum content length. Each flush creates a new file container, uploads to it, and pushes 
   * it to the stream source.
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
