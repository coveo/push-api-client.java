package com.coveo.pushapiclient;

import java.io.IOException;

// TODO: LENS-851 - Make public
class DocumentUploadQueue {
    private final UpdloadStrategy uploader;

    public DocumentUploadQueue(UpdloadStrategy uploader) {
        this.uploader = uploader;
    }

    public void flush() throws IOException, InterruptedException {
        throw new UnsupportedOperationException("Unimplemented method (TODO: LENS-856)");
    }

    public void add(DocumentBuilder document) throws IOException, InterruptedException {
        throw new UnsupportedOperationException("Unimplemented method (TODO: LENS-856)");
    }

}
