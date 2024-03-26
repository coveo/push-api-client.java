package com.coveo.pushapiclient;

import com.google.gson.JsonObject;
import java.util.Arrays;

public class StreamUpdateRecord extends BatchUpdateRecord {

  private final JsonObject[] partialUpdate;

  public StreamUpdateRecord(
      JsonObject[] addOrUpdate, JsonObject[] delete, JsonObject[] partialUpdate) {
    super(addOrUpdate, delete);
    this.partialUpdate = partialUpdate;
  }

  public JsonObject[] getPartialUpdate() {
    return partialUpdate;
  }

  @Override
  public String toString() {
    return "StreamUpdateRecord["
        + "addOrUpdate="
        + Arrays.toString(this.getAddOrUpdate())
        + ", delete="
        + Arrays.toString(this.getDelete())
        + ", partialUpdate="
        + Arrays.toString(partialUpdate)
        + ']';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    StreamUpdateRecord that = (StreamUpdateRecord) obj;
    return Arrays.equals(this.getAddOrUpdate(), that.getAddOrUpdate())
        && Arrays.equals(this.getDelete(), that.getDelete())
        && Arrays.equals(partialUpdate, that.partialUpdate);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + Arrays.hashCode(partialUpdate);
    return result;
  }
}
