package com.coveo.pushapiclient;

public class BackoffOptionsBuilder {
  public static final int DEFAULT_RETRY_AFTER = 5000;
  public static final int DEFAULT_MAX_RETRIES = 10;
  public static final int DEFAULT_TIME_MULTIPLE = 2;

  private int retryAfter = DEFAULT_RETRY_AFTER;
  private int maxRetries = DEFAULT_MAX_RETRIES;
  private int timeMultiple = DEFAULT_TIME_MULTIPLE;

  public BackoffOptionsBuilder withRetryAfter(int retryAfter) {
    this.retryAfter = retryAfter;
    return this;
  }

  public BackoffOptionsBuilder withMaxRetries(int maxRetries) {
    this.maxRetries = maxRetries;
    return this;
  }

  public BackoffOptionsBuilder withTimeMultiple(int timeMultiple) {
    this.timeMultiple = timeMultiple;
    return this;
  }

  public BackoffOptions build() {
    return new BackoffOptions(this.retryAfter, this.maxRetries, this.timeMultiple);
  }
}
