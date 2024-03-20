package com.coveo.pushapiclient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Map;

public class PartialUpdateDocument {

  /** The documentId of the document. */
  public String documentId;

  /** The operator of the document. */
  public PartialUpdateOperator operator;

  /** The field to update. */
  public String field;

  /** The value of the field to be updated. */
  public Object value;

  public JsonObject marshalJsonObject() {
    return new Gson().toJsonTree(this).getAsJsonObject();
  }

  /**
   * Creates a new PartialUpdateDocument. The type of the value provided is constrained by the
   * operator.
   *
   * <ul>
   *   <li>PartialUpdateOperator.ARRAY_APPEND: value must be an array
   *   <li>PartialUpdateOperator.ARRAY_REMOVE: value must be an array
   *   <li>PartialUpdateOperator.FIELD_VALUE_REPLACE: value can be any type
   *   <li>PartialUpdateOperator.DICTIONARY_PUT: value must be a Map
   *   <li>PartialUpdateOperator.DICTIONARY_REMOVE: value must be a String or an Array
   * </ul>
   *
   * @param documentId The id of the document.
   * @param operator The operator to use.
   * @param field The field to update.
   * @param value The value to update the field with.
   */
  public PartialUpdateDocument(
      String documentId, PartialUpdateOperator operator, String field, Object value) {
    if (value == null) throw new IllegalArgumentException("Value cannot be null");
    if (operator == null) throw new IllegalArgumentException("Operator cannot be null");
    if (field == null) throw new IllegalArgumentException("Field cannot be null");
    if (documentId == null) throw new IllegalArgumentException("DocumentId cannot be null");

    this.documentId = documentId;
    this.operator = operator;
    this.field = field;

    switch (operator) {
      case ARRAYAPPEND:
      case ARRAYREMOVE:
        if (!value.getClass().isArray())
          throw new IllegalArgumentException("Value must be an array for operator " + operator);
        break;
      case FIELDVALUEREPLACE:
        break;
      case DICTIONARYPUT:
        if (!(value instanceof Map<?, ?>))
          throw new IllegalArgumentException("Value must be a Map for operator " + operator);
        break;
      case DICTIONARYREMOVE:
        if (!(value instanceof String) && !value.getClass().isArray())
          throw new IllegalArgumentException(
              "Value must be a String or an Array for operator " + operator);
        break;
      default:
        throw new IllegalArgumentException("Invalid operator " + operator);
    }
    this.value = value;
  }
}
