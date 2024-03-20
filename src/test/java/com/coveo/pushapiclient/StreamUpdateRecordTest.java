package com.coveo.pushapiclient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class StreamUpdateRecordTest {

  private StreamUpdateRecord sur1;
  private StreamUpdateRecord sur2;
  private StreamUpdateRecord sur3;
  private StreamUpdateRecord sur4;

  @Before
  public void setUp() throws Exception {
    Gson gson = new Gson();
    JsonObject json1 = gson.fromJson("{ \"key\": \"value1\" }", JsonObject.class);
    JsonObject json2 = gson.fromJson("{ \"key\": \"value1\" }", JsonObject.class);
    JsonObject json3 = gson.fromJson("{ \"key3\": \"value3\" }", JsonObject.class);
    JsonObject json4 = gson.fromJson("{ \"key4\": \"value4\" }", JsonObject.class);
    JsonObject json5 = gson.fromJson("{ \"key4\": \"value4\" }", JsonObject.class);
    JsonObject json6 = gson.fromJson("{ \"key4\": \"value4\" }", JsonObject.class);

    JsonObject[] jsonArray1 = new JsonObject[] {json1, json3};
    JsonObject[] jsonArray2 = new JsonObject[] {json2, json3};
    JsonObject[] jsonArray3 = new JsonObject[] {json1, json3};
    JsonObject[] jsonArray4 = new JsonObject[] {json4, json3};
    JsonObject[] jsonArray5 = new JsonObject[] {json5, json6};
    JsonObject[] jsonArray6 = new JsonObject[] {json5, json3};

    sur1 = new StreamUpdateRecord(jsonArray1, jsonArray2, jsonArray5);
    sur2 = new StreamUpdateRecord(jsonArray1, jsonArray2, jsonArray5);
    sur3 = new StreamUpdateRecord(jsonArray3, jsonArray4, jsonArray6);
    sur4 = sur1;
  }

  @Test
  public void testToString() {
    assertEquals(sur1.toString(), sur1.toString());
    assertEquals(sur1.toString(), sur2.toString());
    assertEquals(sur1.toString(), sur4.toString());
    assertNotEquals(sur1.toString(), sur3.toString());
  }

  @Test
  public void testEquals() {
    assertTrue(sur1.equals(sur1));
    assertTrue(sur1.equals(sur2));
    assertFalse(sur1.equals(sur3));
    assertTrue(sur1.equals(sur4));
    assertFalse(sur1.equals(null));
  }

  @Test
  public void testHashCode() {
    assertEquals(sur1.hashCode(), sur1.hashCode());
    assertEquals(sur1.hashCode(), sur2.hashCode());
    assertEquals(sur1.hashCode(), sur4.hashCode());
    assertEquals(sur3.hashCode(), sur3.hashCode());
    assertNotEquals(sur1.hashCode(), sur3.hashCode());
    assertNotEquals(sur2.hashCode(), sur3.hashCode());
  }
}
