package com.coveo.pushapiclient;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class BatchUpdateTest {

    private BatchUpdate batch1;
    private BatchUpdate batch2;
    private BatchUpdate batch3;
    private BatchUpdate batch4;

    @Before
    public void setUp() {
        DocumentBuilder db1 = new DocumentBuilder("some_uri", "some_title");
        DocumentBuilder db2 = new DocumentBuilder("some_uri", "some_title");
        DocumentBuilder db3 = new DocumentBuilder("some_other_uri", "some_title");
        DocumentBuilder db4 = new DocumentBuilder("some_uri", "some_other_title");

        List<DocumentBuilder> list1 = new ArrayList<>();
        list1.add(db1);
        list1.add(db3);

        List<DocumentBuilder> list2 = new ArrayList<>();
        list2.add(db1);
        list2.add(db4);

        List<DocumentBuilder> list3 = new ArrayList<>();
        list3.add(db2);
        list3.add(db3);
        list3.add(db4);

        DeleteDocument del1 = new DeleteDocument("123");
        DeleteDocument del2 = new DeleteDocument("456");
        DeleteDocument del3 = new DeleteDocument("789");

        List<DeleteDocument> delList1 = new ArrayList<>();
        delList1.add(del1);
        delList1.add(del2);

        List<DeleteDocument> delList2 = new ArrayList<>();
        delList2.add(del2);
        delList2.add(del3);

        batch1 = new BatchUpdate(list1, delList1);
        batch2 = new BatchUpdate(list1, delList1);
        batch3 = new BatchUpdate(list2, delList2);
        batch4 = batch1;
    }

    @Test
    public void testToString() {
        assertEquals(batch1.toString(), batch1.toString());
        assertEquals(batch1.toString(), batch2.toString());
        assertEquals(batch1.toString(), batch4.toString());
        assertNotEquals(batch1.toString(), batch3.toString());
    }

    @Test
    public void testEquals() {
        assertFalse(batch1.equals(null));
        assertTrue(batch1.equals(batch1));
        assertTrue(batch1.equals(batch2));
        assertTrue(batch1.equals(batch4));
        assertFalse(batch1.equals(batch3));
    }

    @Test
    public void testHashCode() {
        assertEquals(batch1.hashCode(), batch1.hashCode());
        assertEquals(batch1.hashCode(), batch2.hashCode());
        assertEquals(batch1.hashCode(), batch4.hashCode());
        assertNotEquals(batch1.hashCode(), batch3.hashCode());
    }
}