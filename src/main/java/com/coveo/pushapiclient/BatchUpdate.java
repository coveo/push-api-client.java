package com.coveo.pushapiclient;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * See [Manage Batches of Items in a Push Source](https://docs.coveo.com/en/90)
 */
public record BatchUpdate(List<DocumentBuilder> addOrUpdate, List<DocumentBuilder> delete) {
    public BatchUpdateRecord marshal() {
        return new BatchUpdateRecord(
                this.addOrUpdate.stream().map(documentBuilder -> documentBuilder.marshalJsonObject()).toArray(JsonObject[]::new),
                this.delete.stream().map(documentBuilder -> documentBuilder.marshalJsonObject()).toArray(JsonObject[]::new)
        );
    }
}
