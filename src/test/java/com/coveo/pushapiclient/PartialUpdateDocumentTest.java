package com.coveo.pushapiclient;

import com.google.gson.Gson;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Map;

public class PartialUpdateDocumentTest {

    @Test
    public void shouldCreatePartialUpdateDocumentWithArrayAppendOperator() {
        String[] value = {"value1", "value2"};
        PartialUpdateDocument document = new PartialUpdateDocument("doc1", PartialUpdateOperator.ARRAYAPPEND, "field1", value);
        assertEquals("doc1", document.documentId);
        assertEquals(PartialUpdateOperator.ARRAYAPPEND, document.operator);
        assertEquals("field1", document.field);
        assertArrayEquals(value, (String[]) document.value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenValueIsNotArrayForArrayAppendOperator() {
        PartialUpdateDocument document = new PartialUpdateDocument("doc1", PartialUpdateOperator.ARRAYAPPEND, "field1", "value1");
    }

    @Test
    public void shouldCreatePartialUpdateDocumentWithFieldValueReplaceOperator() {
        String value = "value1";
        PartialUpdateDocument document = new PartialUpdateDocument("doc1", PartialUpdateOperator.FIELDVALUEREPLACE, "field1", value);
        assertEquals("doc1", document.documentId);
        assertEquals(PartialUpdateOperator.FIELDVALUEREPLACE, document.operator);
        assertEquals("field1", document.field);
        assertEquals(value, document.value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenValueIsNull() {
        PartialUpdateDocument document = new PartialUpdateDocument("doc1", PartialUpdateOperator.FIELDVALUEREPLACE, "field1", null);
    }

    @Test
    public void shouldCreatePartialUpdateDocumentWithDictionaryPutOperator() {
        Map<String, String> value = Map.of("key1", "value1");
        PartialUpdateDocument document = new PartialUpdateDocument("doc1", PartialUpdateOperator.DICTIONARYPUT, "field1", value);
        assertEquals("doc1", document.documentId);
        assertEquals(PartialUpdateOperator.DICTIONARYPUT, document.operator);
        assertEquals("field1", document.field);
        assertEquals(value, document.value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenValueIsNotJsonForDictionaryPutOperator() {
        PartialUpdateDocument document = new PartialUpdateDocument("doc1", PartialUpdateOperator.DICTIONARYPUT, "field1", "value1");
    }

    @Test
    public void shouldCreatePartialUpdateDocumentWithDictionaryRemoveOperator() {
        String value = "value1";
        PartialUpdateDocument document = new PartialUpdateDocument("doc1", PartialUpdateOperator.DICTIONARYREMOVE, "field1", value);
        assertEquals("doc1", document.documentId);
        assertEquals(PartialUpdateOperator.DICTIONARYREMOVE, document.operator);
        assertEquals("field1", document.field);
        assertEquals(value, document.value);

        String[] value2 = {"value1", "value2"};;
        PartialUpdateDocument document2 = new PartialUpdateDocument("doc2", PartialUpdateOperator.DICTIONARYREMOVE, "field2", value2);
        assertEquals("doc2", document2.documentId);
        assertEquals(PartialUpdateOperator.DICTIONARYREMOVE, document2.operator);
        assertEquals("field2", document2.field);
        assertEquals(value2, document2.value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenValueIsNotStringOrArrayForDictionaryRemoveOperator() {
        PartialUpdateDocument document = new PartialUpdateDocument("doc1", PartialUpdateOperator.DICTIONARYREMOVE, "field1", 123);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenInvalidOperatorIsUsed() {
        PartialUpdateDocument document = new PartialUpdateDocument("doc1", null, "field1", "value1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenDocumentIdIsNull() {
        PartialUpdateDocument document = new PartialUpdateDocument(null, PartialUpdateOperator.ARRAYAPPEND, "field1", "value1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenFieldIsNull() {
        PartialUpdateDocument document = new PartialUpdateDocument("doc1", PartialUpdateOperator.ARRAYAPPEND, null, "value1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenOperatorIsNull() {
        PartialUpdateDocument document = new PartialUpdateDocument("doc1", null, "field1", "value1");
    }
}