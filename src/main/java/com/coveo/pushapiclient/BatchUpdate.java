package com.coveo.pushapiclient;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * See [Manage Batches of Items in a Push Source](https://docs.coveo.com/en/90)
 */
public class BatchUpdate {

    private List<DocumentBuilder> addOrUpdate;
    private List<DeleteDocument> delete;

    public BatchUpdate(List<DocumentBuilder> addOrUpdate, List<DeleteDocument> delete) {
        this.addOrUpdate = addOrUpdate;
        this.delete = delete;
    }

    public BatchUpdateRecord marshal() {
        return new BatchUpdateRecord(
                this.addOrUpdate.stream().map(DocumentBuilder::marshalJsonObject).toArray(JsonObject[]::new),
                this.delete.stream().map(DeleteDocument::marshalJsonObject).toArray(JsonObject[]::new)
        );
    }

    public List<DocumentBuilder> getAddOrUpdate() {
        return addOrUpdate;
    }

    public List<DeleteDocument> getDelete() {
        return delete;
    }
}
