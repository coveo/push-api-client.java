package com.coveo.pushapiclient;

import java.io.IOException;

/**
 * Represents a queue for uploading documents using a specified upload strategy
 * and accumulator.
 */
class DocumentUploadQueue {
    private final UpdloadStrategy uploader;
    private final BatchUpdateAccumulator accumulator;

    /**
     * Constructs a new DocumentUploadQueue object.
     *
     * @param uploader    The upload strategy to be used for document uploads.
     * @param accumulator The accumulator for queuing documents to be uploaded.
     */
    public DocumentUploadQueue(UpdloadStrategy uploader, BatchUpdateAccumulator accumulator) {
        this.uploader = uploader;
        this.accumulator = accumulator;
    }

    /**
     * Flushes the accumulated documents by applying the upload strategy.
     *
     * @throws IOException          If an I/O error occurs during the upload.
     * @throws InterruptedException If the upload process is interrupted.
     */
    public void flush() throws IOException, InterruptedException {
        if (!this.accumulator.isEmpty()) {
            BatchUpdate batch = this.accumulator.getBatch();
            // TODO: LENS-871: support concurrent requests
            this.uploader.apply(batch);
        }
        this.accumulator.resetBatch();
        this.accumulator.setSize(0);
    }

    /**
     * Adds a {@link DocumentBuilder} to the upload queue and flushes the queue if
     * it exceeds the maximum content length.
     * See {@link DocumentUploadQueue#flush}.
     *
     * @param document              The document to be added to the index.
     * @throws IOException          If an I/O error occurs during the upload.
     * @throws InterruptedException If the upload process is interrupted.
     */
    public void add(DocumentBuilder document) throws IOException, InterruptedException {
        final int sizeOfDoc = document.marshal().getBytes().length;
        if (accumulator.getSize() + sizeOfDoc >= BatchUpdateAccumulator.maxContentLength) {
            this.flush();
        }
        this.accumulator.addToBatch(document);
        this.accumulator.setSize(sizeOfDoc);
    }

    /**
     * Adds a {@link DeleteDocument} to the upload queue and flushes the queue if
     * it exceeds the maximum content length.
     * See {@link DocumentUploadQueue#flush}.
     *
     * @param document              The document to be delete from the index.
     * @throws IOException          If an I/O error occurs during the upload.
     * @throws InterruptedException If the upload process is interrupted.
     */
    public void add(DeleteDocument document) throws IOException, InterruptedException {
        final int sizeOfDoc = document.marshalJsonObject().toString().getBytes().length;
        if (accumulator.getSize() + sizeOfDoc >= BatchUpdateAccumulator.maxContentLength) {
            this.flush();
        }
        this.accumulator.addToBatch(document);
        this.accumulator.setSize(sizeOfDoc);
    }

    // TODO: LENS-843: include partial document updates

}
