package com.coveo.pushapiclient;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BackoffOptionsBuilderTest {

  private BackoffOptionsBuilder backoffOptionsBuilder;

  @Before
  public void setup() {
    backoffOptionsBuilder = new BackoffOptionsBuilder();
  }

  @Test
  public void testWithDefaultValues() {
    BackoffOptions backoffOptions = backoffOptionsBuilder.build();
    assertEquals("Should return default retry after time", 5000, backoffOptions.getRetryAfter());
    assertEquals("Should return default max retries", 50, backoffOptions.getMaxRetries());
    assertEquals("Should return default time multiple", 2, backoffOptions.getTimeMultiple());
  }

  @Test
  public void testWithNonDefaultRetryAfter() {
    BackoffOptions backoffOptions = backoffOptionsBuilder.withRetryAfter(1000).build();
    assertEquals("Should return Europe platform URL", 1000, backoffOptions.getRetryAfter());
  }

  @Test
  public void testWithNonDefaultMaxRetries() {
    BackoffOptions backoffOptions = backoffOptionsBuilder.withMaxRetries(15).build();
    assertEquals("Should return the staging platform URL", 15, backoffOptions.getMaxRetries());
  }

  @Test
  public void testWithNonDefaultTimeMultiple() {
    BackoffOptions backoffOptions = backoffOptionsBuilder.withTimeMultiple(3).build();
    assertEquals(3, backoffOptions.getTimeMultiple());
  }
}
