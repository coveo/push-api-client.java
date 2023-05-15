package com.coveo.pushapiclient;

public class FullUploadStream implements AutoCloseable {

    public FullUploadStream(StreamSource source) {
    }

    /**
     * Pushes document to the source.
     * If multiple documents are added, the class will ensure documents are
     * automatically batched into chunks that do not exceed API limit size.
     *
     * @param document
     */
    public void add(DocumentBuilder document) {

    }

    /**
     * Closes the resource, relinquishing any underlying resources.
     * Send any previous documents buffered and not yet sent to the API.
     * Close any opened stream.
     */
    @Override
    public void close() throws Exception {
    }

}
