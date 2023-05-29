package com.coveo.pushapiclient;

import java.util.ArrayList;
import java.util.List;

/**
 * Accumulates documents to be added or deleted in a queue for batch updates.
 */
class BatchUpdateAccumulator {
    static final int maxContentLength = 5 * 1024 * 1024;
    private List<DocumentBuilder> documentToAddList;
    private List<DeleteDocument> documentToDeleteList;
    private int size;

    public BatchUpdateAccumulator() {
        documentToAddList = new ArrayList<>();
        documentToDeleteList = new ArrayList<>();
        // TODO: LENS-843: include partial document updates
    }

    public void resetBatch() {
        this.size = 0;
    }

    public BatchUpdate getBatch() {
        return new BatchUpdate(this.documentToAddList, this.documentToDeleteList);
    }

    public void addToBatch(DocumentBuilder document) {
        documentToAddList.add(document);
    }

    public void addToBatch(DeleteDocument document) {
        documentToDeleteList.add(document);
    }

    public boolean isEmpty() {
        // TODO: LENS-843: include partial document updates
        return documentToAddList.isEmpty() && documentToDeleteList.isEmpty();
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}