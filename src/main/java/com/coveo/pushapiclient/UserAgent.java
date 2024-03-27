package com.coveo.pushapiclient;

public enum UserAgent {
  SAP_COMMERCE_CLOUD_V1 {
    @Override
    public String toString() {
      return "SAPCommerceCloud/v1";
    }
  },
  SAP_COMMERCE_CLOUD_V2 {
    @Override
    public String toString() {
      return "SAPCommerceCloud/v2";
    }
  },
  SAP_COMMERCE_CLOUD_V3 {
    @Override
    public String toString() {
      return "SAPCommerceCloud/v3";
    }
  }
}
