package com.coveo.pushapiclient;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Represents a queue for uploading documents using a specified upload strategy
 */
class DocumentUploadQueue {
    private final UploadStrategy uploader;
    private final int maxQueueSize = 5 * 1024 * 1024;
    private ArrayList<DocumentBuilder> documentToAddList;
    private ArrayList<DeleteDocument> documentToDeleteList;
    private int size;

    /**
     * Constructs a new DocumentUploadQueue object with a default maximum queue size
     * limit of 5MB.
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
     * @throws IOException          If an I/O error occurs during the upload.
     * @throws InterruptedException If the upload process is interrupted.
     */
    public void flush() throws IOException, InterruptedException {
        if (this.isEmpty()) {
            return;
        }
        BatchUpdate batch = this.getBatch();
        // TODO: LENS-871: support concurrent requests
        this.uploader.apply(batch);
        this.size = 0;
        this.documentToAddList.clear();
        this.documentToDeleteList.clear();
    }

    /**
     * Adds a {@link DocumentBuilder} to the upload queue and flushes the queue if
     * it exceeds the maximum content length.
     * See {@link DocumentUploadQueue#flush}.
     *
     * @param document The document to be added to the index.
     * @throws IOException          If an I/O error occurs during the upload.
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
        if (document != null) {
            documentToAddList.add(document);
            this.size += sizeOfDoc;
        }
    }

    /**
     * Adds a {@link DeleteDocument} to the upload queue and flushes the queue if
     * it exceeds the maximum content length.
     * See {@link DocumentUploadQueue#flush}.
     *
     * @param document The document to be delete from the index.
     * @throws IOException          If an I/O error occurs during the upload.
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
        if (document != null) {
            documentToDeleteList.add(document);
            this.size += sizeOfDoc;
        }
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
