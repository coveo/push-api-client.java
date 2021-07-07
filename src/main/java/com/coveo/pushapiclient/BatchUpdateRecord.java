package com.coveo.pushapiclient;

import com.google.gson.JsonObject;

/**
 * See https://docs.coveo.com/en/75
 */
public record BatchUpdateRecord(JsonObject[] addOrUpdate, JsonObject[] delete) {
}
