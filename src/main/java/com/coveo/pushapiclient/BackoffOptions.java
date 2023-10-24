package com.coveo.pushapiclient;

public class BackoffOptions {
  private final int retryAfter;
  private final int maxRetries;
  private final int timeMultiple;

  /**
   * @param retryAfter The amount of time, in milliseconds, to wait between throttled request
   *     attempts.
   * @param maxRetries The maximum number of times to retry throttled requests.
   * @param timeMultiple The multiple by which to increase the wait time between each throttled
   *     request attempt.
   */
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
