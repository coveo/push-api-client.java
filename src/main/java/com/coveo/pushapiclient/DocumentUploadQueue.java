package com.coveo.pushapiclient;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Represents a queue for uploading documents using a specified upload strategy */
class DocumentUploadQueue<T extends BatchUpdate> {
  private static final Logger logger = LogManager.getLogger(DocumentUploadQueue.class);

  /** Maximum allowed queue size based on Stream API limit (256 MB) */
  protected static final int MAX_ALLOWED_QUEUE_SIZE = 256 * 1024 * 1024;

  /** Default queue size (5 MB) */
  protected static final int DEFAULT_QUEUE_SIZE = 5 * 1024 * 1024;

  /** System property name for configuring the default batch size */
  public static final String BATCH_SIZE_PROPERTY = "coveo.push.batchSize";

  protected UploadStrategy<T> uploader;
  protected final int maxQueueSize;
  protected ArrayList<DocumentBuilder> documentToAddList;
  protected ArrayList<DeleteDocument> documentToDeleteList;
  protected int size;

  /**
   * Validates batch size against constraints (> 0 and <= 256MB). Used by getConfiguredBatchSize and
   * constructors to ensure consistent validation logic.
   *
   * @param sizeBytes The batch size in bytes to validate
   * @throws IllegalArgumentException if size exceeds MAX_ALLOWED_QUEUE_SIZE or is <= 0
   */
  protected static void validateBatchSize(int sizeBytes) {
    if (sizeBytes > MAX_ALLOWED_QUEUE_SIZE) {
      throw new IllegalArgumentException(
          String.format(
              "Batch size (%d bytes) exceeds the Stream API limit of %d bytes (%d MB)",
              sizeBytes, MAX_ALLOWED_QUEUE_SIZE, MAX_ALLOWED_QUEUE_SIZE / (1024 * 1024)));
    }
    if (sizeBytes <= 0) {
      throw new IllegalArgumentException("Batch size must be greater than 0");
    }
  }

  /**
   * Gets the configured batch size from system properties, or returns the default if not set.
   *
   * <p>The system property is read as bytes. When not set, returns DEFAULT_QUEUE_SIZE (5 MB).
   *
   * <p>Example: Set a 50 MB batch size via system property:
   *
   * <pre>
   *   java -Dcoveo.push.batchSize=52428800 -jar app.jar  // 50 * 1024 * 1024 bytes
   * </pre>
   *
   * @return The configured batch size in bytes (e.g., 52428800 for 50 MB)
   * @throws IllegalArgumentException if the configured value exceeds 256MB or is invalid
   */
  public static int getConfiguredBatchSize() {
    String propertyValue = System.getProperty(BATCH_SIZE_PROPERTY);
    if (propertyValue == null || propertyValue.trim().isEmpty()) {
      return DEFAULT_QUEUE_SIZE;
    }

    int configuredSize;
    try {
      configuredSize = Integer.parseInt(propertyValue.trim());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException(
          String.format(
              "Invalid value for system property %s: '%s'. Must be a valid integer.",
              BATCH_SIZE_PROPERTY, propertyValue),
          e);
    }

    validateBatchSize(configuredSize);

    logger.info(
        String.format(
            "Using configured batch size from system property %s: %d bytes (%.2f MB)",
            BATCH_SIZE_PROPERTY, configuredSize, configuredSize / (1024.0 * 1024.0)));
    return configuredSize;
  }

  /**
   * Constructs a new DocumentUploadQueue with the default batch size.
   *
   * <p>Uses the configured batch size from system property "coveo.push.batchSize" if set, otherwise
   * defaults to DEFAULT_QUEUE_SIZE (5 MB = 5242880 bytes).
   *
   * @param uploader The upload strategy to be used for document uploads.
   * @throws IllegalArgumentException if the system property value exceeds 256MB or is invalid.
   */
  public DocumentUploadQueue(UploadStrategy<T> uploader) {
    this(uploader, getConfiguredBatchSize());
  }

  /**
   * Constructs a new DocumentUploadQueue object with a configurable maximum queue size limit.
   *
   * @param uploader The upload strategy to be used for document uploads.
   * @param maxQueueSize The maximum queue size in bytes (e.g., 52428800 for 50 MB). Must not exceed
   *     256MB (Stream API limit).
   * @throws IllegalArgumentException if maxQueueSize exceeds the API limit of 256MB.
   */
  public DocumentUploadQueue(UploadStrategy<T> uploader, int maxQueueSize) {
    validateBatchSize(maxQueueSize);
    this.documentToAddList = new ArrayList<>();
    this.documentToDeleteList = new ArrayList<>();
    this.uploader = uploader;
    this.maxQueueSize = maxQueueSize;
  }

  /**
   * Default constructor for testing purposes (used by Mockito @InjectMocks). Initializes with
   * default batch size; uploader is injected by Mockito.
   */
  public DocumentUploadQueue() {
    this.documentToAddList = new ArrayList<>();
    this.documentToDeleteList = new ArrayList<>();
    this.maxQueueSize = DEFAULT_QUEUE_SIZE;
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
    @SuppressWarnings("unchecked")
    T batch = (T) this.getBatch();
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
      logger.debug("Adding document to batch: " + document.getDocument().uri);
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
