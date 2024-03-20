package com.coveo.pushapiclient;

public enum PartialUpdateOperator {
  ARRAYAPPEND {
    public String toString() {
      return "arrayAppend";
    }
  },
  ARRAYREMOVE {
    public String toString() {
      return "arrayRemove";
    }
  },
  FIELDVALUEREPLACE {
    public String toString() {
      return "fieldValueReplace";
    }
  },
  DICTIONARYPUT {
    public String toString() {
      return "dictionaryPut";
    }
  },
  DICTIONARYREMOVE {
    public String toString() {
      return "dictionaryRemove";
    }
  }
}
