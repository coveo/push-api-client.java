package com.coveo.pushapiclient;

public class BackoffOptionsBuilder {

  private int retryAfter = BackoffOptions.DEFAULT_RETRY_AFTER;
  private int maxRetries = BackoffOptions.DEFAULT_MAX_RETRIES;
  private int timeMultiple = BackoffOptions.DEFAULT_TIME_MULTIPLE;

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
