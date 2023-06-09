package com.coveo.pushapiclient;

/** Available environments to use as the host for the PushAPI. */
public enum Environment {
  PRODUCTION("prod"),
  HIPAA("hipaa"),
  DEVELOPMENT("dev"),
  STAGING("stg");

  private String value;

  Environment(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
