package com.coveo.pushapiclient;

import com.google.gson.JsonObject;

import java.util.List;

public class BatchUpdate {
    private List<DocumentBuilder> addOrUpdate;
    private List<DocumentBuilder> delete;

    public BatchUpdate(List<DocumentBuilder> addOrUpdate, List<DocumentBuilder> delete) {
        this.addOrUpdate = addOrUpdate;
        this.delete = delete;
    }

    public BatchUpdateRecord marshal() {
        return new BatchUpdateRecord(
                this.addOrUpdate.stream().map(documentBuilder -> documentBuilder.marshalJsonObject()).toArray(JsonObject[]::new),
                this.delete.stream().map(documentBuilder -> documentBuilder.marshalJsonObject()).toArray(JsonObject[]::new)
        );
    }
}
