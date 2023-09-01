package com.coveo.pushapiclient;

public class BackoffOptions {
  public static final int DEFAULT_RETRY_AFTER = 5000;
  public static final int DEFAULT_MAX_RETRIES = 50;
  public static final int DEFAULT_TIME_MULTIPLE = 2;

  private final int retryAfter;
  private final int maxRetries;
  private final int timeMultiple;

  public BackoffOptions(int retryAfter, int maxRetries, int timeMultiple) {
    this.retryAfter = DEFAULT_RETRY_AFTER;
    this.maxRetries = DEFAULT_MAX_RETRIES;
    this.timeMultiple = DEFAULT_TIME_MULTIPLE;
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
