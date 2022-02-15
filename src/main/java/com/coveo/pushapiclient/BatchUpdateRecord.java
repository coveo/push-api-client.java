package com.coveo.pushapiclient;

import com.google.gson.JsonObject;

/**
 * See [BatchDocumentBody](https://docs.coveo.com/en/75/#batchdocumentbody)
 */
public class BatchUpdateRecord {

    private JsonObject[] addOrUpdate;
    private JsonObject[] delete;

    public BatchUpdateRecord(JsonObject[] addOrUpdate, JsonObject[] delete) {
        this.addOrUpdate = addOrUpdate;
        this.delete = delete;
    }

    public JsonObject[] getAddOrUpdate() {
        return addOrUpdate;
    }

    public JsonObject[] getDelete() {
        return delete;
    }
}
