package com.coveo.pushapiclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a queue for uploading documents using a specified upload strategy
 */
class DocumentUploadQueue {
    static final int maxContentLength = 5 * 1024 * 1024;
    private final UpdloadStrategy uploader;

    private List<DocumentBuilder> documentToAddList;
    private List<DeleteDocument> documentToDeleteList;
    private int size;

    /**
     * Constructs a new DocumentUploadQueue object.
     *
     * @param uploader The upload strategy to be used for document uploads.
     */
    public DocumentUploadQueue(UpdloadStrategy uploader) {
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
        final int sizeOfDoc = document.marshal().getBytes().length;
        if (!this.isEmpty() && this.size + sizeOfDoc >= maxContentLength) {
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
        final int sizeOfDoc = document.marshalJsonObject().toString().getBytes().length;
        if (!this.isEmpty() && this.size + sizeOfDoc >= maxContentLength) {
            this.flush();
        }
        if (document != null) {
            documentToDeleteList.add(document);
            this.size += sizeOfDoc;
        }
    }

    private BatchUpdate getBatch() {
        return new BatchUpdate(this.documentToAddList, this.documentToDeleteList);
    }

    private boolean isEmpty() {
        // TODO: LENS-843: include partial document updates
        return documentToAddList.isEmpty() && documentToDeleteList.isEmpty();
    }

}
