package com.coveo.pushapiclient;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SecurityIdentityBatchConfigTest {

  private SecurityIdentityBatchConfig config1;
  private SecurityIdentityBatchConfig config2;
  private SecurityIdentityBatchConfig config3;
  private SecurityIdentityBatchConfig config4;
  private SecurityIdentityBatchConfig config5;

  @Before
  public void setUp() {
    config1 = new SecurityIdentityBatchConfig("some_file_id", 123L);
    config2 = new SecurityIdentityBatchConfig("some_file_id", 123L);
    config3 = new SecurityIdentityBatchConfig("some_other_file_id", 123L);
    config4 = new SecurityIdentityBatchConfig("some_file_id", 456L);
    config5 = config1;
  }

  @Test
  public void testToString() {
    assertEquals(config1.toString(), config1.toString());
    assertEquals(config1.toString(), config2.toString());
    assertEquals(config1.toString(), config5.toString());
    assertEquals(config3.toString(), config3.toString());
    assertNotEquals(config1.toString(), config3.toString());
    assertNotEquals(config3.toString(), config4.toString());
  }

  @Test
  public void testEquals() {
    assertFalse(config1.equals(null));
    assertTrue(config1.equals(config1));
    assertTrue(config1.equals(config2));
    assertTrue(config1.equals(config5));
    assertFalse(config1.equals(config3));
    assertFalse(config3.equals(config4));
  }

  @Test
  public void testHashCode() {
    assertEquals(config1.hashCode(), config1.hashCode());
    assertEquals(config1.hashCode(), config2.hashCode());
    assertEquals(config1.hashCode(), config5.hashCode());
    assertEquals(config3.hashCode(), config3.hashCode());
    assertNotEquals(config1.hashCode(), config3.hashCode());
    assertNotEquals(config3.hashCode(), config4.hashCode());
  }
}
