package com.coveo.pushapiclient;

import com.google.gson.JsonObject;

/**
 * See [BatchDocumentBody](https://docs.coveo.com/en/75/#batchdocumentbody)
 */
public record BatchUpdateRecord(JsonObject[] addOrUpdate, JsonObject[] delete) {
}
