package com.coveo.pushapiclient;

public enum StreamOperationType {
  REBUILD("rebuild"),
  INCREMENTAL("incremental");

  private final String value;

  StreamOperationType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
