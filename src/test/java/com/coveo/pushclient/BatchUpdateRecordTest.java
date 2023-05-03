package com.coveo.pushclient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BatchUpdateRecordTest {

    private BatchUpdateRecord bur1;
    private BatchUpdateRecord bur2;
    private BatchUpdateRecord bur3;
    private BatchUpdateRecord bur4;

    @Before
    public void setUp() throws Exception {
        Gson gson = new Gson();
        JsonObject json1 = gson.fromJson("{ \"key\": \"value1\" }", JsonObject.class);
        JsonObject json2 = gson.fromJson("{ \"key\": \"value1\" }", JsonObject.class);
        JsonObject json3 = gson.fromJson("{ \"key3\": \"value3\" }", JsonObject.class);
        JsonObject json4 = gson.fromJson("{ \"key4\": \"value4\" }", JsonObject.class);

        JsonObject[] jsonArray1 = new JsonObject[] {json1, json3};
        JsonObject[] jsonArray2 = new JsonObject[] {json2, json3};
        JsonObject[] jsonArray3 = new JsonObject[] {json1, json3};
        JsonObject[] jsonArray4 = new JsonObject[] {json4, json3};

        bur1 = new BatchUpdateRecord(jsonArray1, jsonArray2);
        bur2 = new BatchUpdateRecord(jsonArray1, jsonArray2);
        bur3 = new BatchUpdateRecord(jsonArray3, jsonArray4);
        bur4 = bur1;
    }

    @Test
    public void testToString() {
        assertEquals(bur1.toString(), bur1.toString());
        assertEquals(bur1.toString(), bur2.toString());
        assertEquals(bur1.toString(), bur4.toString());
        assertNotEquals(bur1.toString(), bur3.toString());
    }

    @Test
    public void testEquals() {
        assertTrue(bur1.equals(bur1));
        assertTrue(bur1.equals(bur2));
        assertFalse(bur1.equals(bur3));
        assertTrue(bur1.equals(bur4));
        assertFalse(bur1.equals(null));
    }

    @Test
    public void testHashCode() {
        assertEquals(bur1.hashCode(), bur1.hashCode());
        assertEquals(bur1.hashCode(), bur2.hashCode());
        assertEquals(bur1.hashCode(), bur4.hashCode());
        assertEquals(bur3.hashCode(), bur3.hashCode());
        assertNotEquals(bur1.hashCode(), bur3.hashCode());
        assertNotEquals(bur2.hashCode(), bur3.hashCode());
    }
}