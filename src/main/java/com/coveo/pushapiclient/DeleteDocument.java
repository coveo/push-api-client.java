package com.coveo.pushapiclient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class DeleteDocument {

  /** The documentId of the document. */
  public String documentId;

  /** Flag to delete children of the document. */
  public boolean deleteChildren;

  public DeleteDocument(String documentId) {
    this.documentId = documentId;
    this.deleteChildren = false;
  }

  public DeleteDocument(String documentId, boolean deleteChildren) {
    this.documentId = documentId;
    this.deleteChildren = deleteChildren;
  }

  public JsonObject marshalJsonObject() {
    return new Gson().toJsonTree(this).getAsJsonObject();
  }
}
