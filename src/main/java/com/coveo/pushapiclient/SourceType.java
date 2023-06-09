package com.coveo.pushapiclient;

public enum SourceType implements SourceTypeInterface {
  PUSH {
    public String toString() {
      return "PUSH";
    }

    public boolean isPushEnabled() {
      return true;
    }

    @Override
    public boolean isStreamEnabled() {
      return false;
    }
  },
  CATALOG {
    public String toString() {
      return "CATALOG";
    }

    @Override
    public boolean isPushEnabled() {
      return true;
    }

    @Override
    public boolean isStreamEnabled() {
      return true;
    }
  },
}

interface SourceTypeInterface {

  String toString();

  boolean isPushEnabled();

  boolean isStreamEnabled();
}
