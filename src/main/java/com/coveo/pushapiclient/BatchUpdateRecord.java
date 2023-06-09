package com.coveo.pushapiclient;

import com.google.gson.JsonObject;
import java.util.Arrays;

/** See [BatchDocumentBody](https://docs.coveo.com/en/75/#batchdocumentbody) */
public class BatchUpdateRecord {

  private final JsonObject[] addOrUpdate;
  private final JsonObject[] delete;

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

  @Override
  public String toString() {
    return "BatchUpdateRecord["
        + "addOrUpdate="
        + Arrays.toString(addOrUpdate)
        + ", delete="
        + Arrays.toString(delete)
        + ']';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    BatchUpdateRecord that = (BatchUpdateRecord) obj;
    return Arrays.equals(addOrUpdate, that.addOrUpdate) && Arrays.equals(delete, that.delete);
  }

  @Override
  public int hashCode() {
    int result = Arrays.hashCode(addOrUpdate);
    result = 31 * result + Arrays.hashCode(delete);
    return result;
  }
}
