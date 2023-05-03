package com.coveo.pushclient;

import com.coveo.document.CompressedBinaryData;
import com.coveo.document.CompressionType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CompressedBinaryDataTest {

    private CompressedBinaryData data1;
    private CompressedBinaryData data2;

    private CompressedBinaryData data3;
    private CompressedBinaryData data4;
    private CompressedBinaryData data5;

    @Before
    public void setUp() {
        data1 = new CompressedBinaryData("some_data", CompressionType.UNCOMPRESSED);
        data2 = new CompressedBinaryData("some_data", CompressionType.UNCOMPRESSED);

        data3 = new CompressedBinaryData("some_other_data", CompressionType.UNCOMPRESSED);
        data4 = new CompressedBinaryData("some_data", CompressionType.GZIP);
        data5 = data1;
    }

    @Test
    public void testToString() {
        assertEquals(data1.toString(), data1.toString());
        assertEquals(data1.toString(), data2.toString());
        assertEquals(data1.toString(), data5.toString());
    }

    @Test
    public void testEquals() {
        assertTrue(data1.equals(data1));
        assertTrue(data1.equals(data2));
        assertFalse(data1.equals(data3));
        assertFalse(data1.equals(data4));
        assertTrue(data1.equals(data5));
        assertFalse(data1.equals(null));
    }

    @Test
    public void testHashCode() {
        assertEquals(data1.hashCode(), data1.hashCode());
        assertEquals(data3.hashCode(), data3.hashCode());
        assertEquals(data4.hashCode(), data4.hashCode());
        assertEquals(data1.hashCode(), data2.hashCode());
        assertEquals(data1.hashCode(), data5.hashCode());
        assertNotEquals(data1.hashCode(), data3.hashCode());
        assertNotEquals(data1.hashCode(), data4.hashCode());
        assertNotEquals(data3.hashCode(), data4.hashCode());
    }
}