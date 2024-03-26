package com.coveo.pushapiclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class StreamUpdateTest {

  private StreamUpdate stream1;
  private StreamUpdate stream2;
  private StreamUpdate stream3;
  private StreamUpdate stream4;

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

    PartialUpdateDocument partialUpdateDocument1 =
        new PartialUpdateDocument("123", PartialUpdateOperator.FIELDVALUEREPLACE, "field", "value");
    PartialUpdateDocument partialUpdateDocument2 =
        new PartialUpdateDocument(
            "456", PartialUpdateOperator.FIELDVALUEREPLACE, "field2", "value2");
    PartialUpdateDocument partialUpdateDocument3 =
        new PartialUpdateDocument(
            "789", PartialUpdateOperator.FIELDVALUEREPLACE, "field3", "value3");

    List<PartialUpdateDocument> partialUpdateDocuments1 = new ArrayList<>();
    partialUpdateDocuments1.add(partialUpdateDocument1);
    partialUpdateDocuments1.add(partialUpdateDocument2);

    List<PartialUpdateDocument> partialUpdateDocuments2 = new ArrayList<>();
    partialUpdateDocuments2.add(partialUpdateDocument2);
    partialUpdateDocuments2.add(partialUpdateDocument3);

    stream1 = new StreamUpdate(list1, delList1, partialUpdateDocuments1);
    stream2 = new StreamUpdate(list1, delList1, partialUpdateDocuments1);
    stream3 = new StreamUpdate(list2, delList2, partialUpdateDocuments2);
    stream4 = stream1;
  }

  @Test
  public void testToString() {
    assertEquals(stream1.toString(), stream1.toString());
    assertEquals(stream1.toString(), stream2.toString());
    assertEquals(stream1.toString(), stream4.toString());
    assertNotEquals(stream1.toString(), stream3.toString());
  }

  @Test
  public void testEquals() {
    assertFalse(stream1.equals(null));
    assertTrue(stream1.equals(stream1));
    assertTrue(stream1.equals(stream2));
    assertTrue(stream1.equals(stream4));
    assertFalse(stream1.equals(stream3));
  }

  @Test
  public void testHashCode() {
    assertEquals(stream1.hashCode(), stream1.hashCode());
    assertEquals(stream1.hashCode(), stream2.hashCode());
    assertEquals(stream1.hashCode(), stream4.hashCode());
    assertNotEquals(stream1.hashCode(), stream3.hashCode());
  }
}
