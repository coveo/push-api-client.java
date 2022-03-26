package com.coveo.pushapiclient;

import com.google.gson.JsonObject;

/**
 * See [BatchIdentityBody](https://docs.coveo.com/en/139#batchidentitybody)
 */
public class BatchIdentityRecord {

    private JsonObject[] members;
    private JsonObject[] mappings;
    private JsonObject[] deleted;

    public BatchIdentityRecord(JsonObject[] members, JsonObject[] mappings, JsonObject[] deleted) {
        this.members = members;
        this.mappings = mappings;
        this.deleted = deleted;
    }

    public JsonObject[] getMembers() {
        return members;
    }

    public JsonObject[] getMappings() {
        return mappings;
    }

    public JsonObject[] getDeleted() {
        return deleted;
    }
}
