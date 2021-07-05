package com.coveo.pushapiclient;

import com.google.gson.JsonObject;

public record BatchUpdateRecord(JsonObject[] addOrUpdate, JsonObject[] delete) {
}
