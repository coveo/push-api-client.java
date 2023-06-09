package com.coveo.pushapiclient;

public enum SecurityIdentityType {
  UNKNOWN {
    public String toString() {
      return "UNKNOWN";
    }
  },
  USER {
    public String toString() {
      return "USER";
    }
  },
  GROUP {
    public String toString() {
      return "GROUP";
    }
  },
  VIRTUAL_GROUP {
    public String toString() {
      return "VIRTUAL_GROUP";
    }
  }
}
