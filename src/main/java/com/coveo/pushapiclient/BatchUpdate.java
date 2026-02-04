package com.coveo.pushapiclient;

import com.google.gson.JsonObject;
import java.util.List;
import java.util.Objects;

/**
 * @see <a href="https://docs.coveo.com/en/90">Manage Batches of Items in a Push Source</a>
 */
public class BatchUpdate implements UploadContent {

  private final List<DocumentBuilder> addOrUpdate;
  private final List<DeleteDocument> delete;

  public BatchUpdate(List<DocumentBuilder> addOrUpdate, List<DeleteDocument> delete) {
    this.addOrUpdate = addOrUpdate;
    this.delete = delete;
  }

  public BatchUpdateRecord marshal() {
    return new BatchUpdateRecord(
        this.addOrUpdate.stream()
            .map(DocumentBuilder::marshalJsonObject)
            .toArray(JsonObject[]::new),
        this.delete.stream().map(DeleteDocument::marshalJsonObject).toArray(JsonObject[]::new));
  }

  public List<DocumentBuilder> getAddOrUpdate() {
    return addOrUpdate;
  }

  public List<DeleteDocument> getDelete() {
    return delete;
  }

  @Override
  public String toString() {
    return "BatchUpdate[" + "addOrUpdate=" + addOrUpdate + ", delete=" + delete + ']';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    BatchUpdate that = (BatchUpdate) obj;
    return addOrUpdate.equals(that.addOrUpdate) && delete.equals(that.delete);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addOrUpdate, delete);
  }
}
