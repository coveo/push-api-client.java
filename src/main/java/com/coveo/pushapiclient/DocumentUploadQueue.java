package com.coveo.pushapiclient;

import java.io.IOException;

public class DocumentUploadQueue {
    private final UpdloadStrategy uploader;

    public DocumentUploadQueue(UpdloadStrategy uploader) {
        this.uploader = uploader;
    }

    public void flush() throws IOException, InterruptedException {
    }

    public void add(DocumentBuilder documentToAdd, DeleteDocument documentToDelete)
            throws IOException, InterruptedException {
    }

    public void add(DocumentBuilder document) throws IOException, InterruptedException {
        // Once batch is ready, send it like:
        // this.uploader.apply(batchUpdate);
        throw new UnsupportedOperationException("Unimplemented method");
    }

}
