package com.coveo.pushapiclient;

public class BackoffOptions {
  private final int retryAfter;
  private final int maxRetries;
  private final int timeMultiple;

  public BackoffOptions(int retryAfter, int maxRetries, int timeMultiple) {
    this.retryAfter = retryAfter;
    this.maxRetries = maxRetries;
    this.timeMultiple = timeMultiple;
  }

  public int getRetryAfter() {
    return this.retryAfter;
  }

  public int getMaxRetries() {
    return this.maxRetries;
  }

  public int getTimeMultiple() {
    return this.timeMultiple;
  }
}
