package com.coveo.pushapiclient;

public class PushService implements AutoCloseable {

    public PushService(CatalogSource source) {
    }

    public void addOrUpdate(DocumentBuilder document) {
    }

    public void partialUpdate(PartialUpdateDocument document) {
    }

    public void delete(DeleteDocument document) {
    }

    /**
     * Closes the resource, relinquishing any underlying resources.
     * Send any previous documents buffered and not yet sent to the API.
     */
    @Override
    public void close() throws Exception {
    }

}
