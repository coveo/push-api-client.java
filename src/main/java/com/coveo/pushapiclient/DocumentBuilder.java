package com.coveo.pushapiclient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class DocumentBuilder {

    private static final ArrayList<String> reservedKeynames = new ArrayList<>() {{
        add("compressedBinaryData");
        add("compressedBinaryDataFileId");
        add("parentId");
        add("fileExtension");
        add("data");
        add("permissions");
        add("documentId");
        add("orderingId");
    }};

    private final Document document;

    public DocumentBuilder(String uri, String title) {
        this.document = new Document();
        this.document.uri = uri;
        this.document.title = title;
    }

    public Document getDocument() {
        return this.document;
    }

    public DocumentBuilder withData(String data) {
        this.document.data = data;
        return this;
    }

    public DocumentBuilder withDate(String date) {
        DateTime dt = DateTime.parse(date);
        this.document.date = this.dateFormat(dt);
        return this;
    }

    public DocumentBuilder withDate(Long date) {
        DateTime dt = new DateTime(date);
        this.document.date = this.dateFormat(dt);
        return this;
    }

    public DocumentBuilder withDate(Date date) {
        DateTime dt = new DateTime(date);
        this.document.date = this.dateFormat(dt);
        return this;
    }

    public DocumentBuilder withDate(DateTime date) {
        this.document.date = this.dateFormat(date);
        return this;
    }

    public DocumentBuilder withModifiedDate(String date) {
        DateTime dt = DateTime.parse(date);
        this.document.modifiedDate = this.dateFormat(dt);
        return this;
    }

    public DocumentBuilder withModifiedDate(Long date) {
        DateTime dt = new DateTime(date);
        this.document.modifiedDate = this.dateFormat(dt);
        return this;
    }

    public DocumentBuilder withModifiedDate(DateTime date) {
        this.document.modifiedDate = this.dateFormat(date);
        return this;
    }

    public DocumentBuilder withPermanentId(String permanentId) {
        this.document.permanentId = permanentId;
        return this;
    }

    public DocumentBuilder withCompressedBinaryData(CompressedBinaryData compressedBinaryData) {
        this.validateCompressedBinaryData(compressedBinaryData.data);
        this.document.compressedBinaryData = compressedBinaryData;
        return this;
    }

    public DocumentBuilder withFileExtension(String fileExtension) {
        this.validateFileExtension(fileExtension);
        this.document.fileExtension = fileExtension;
        return this;
    }

    public DocumentBuilder withParentID(String parentID) {
        this.document.parentId = parentID;
        return this;
    }

    public DocumentBuilder withClickableUri(String clickableUri) {
        this.document.clickableUri = clickableUri;
        return this;
    }

    public DocumentBuilder withAuthor(String author) {
        this.document.author = author;
        return this;
    }

    public DocumentBuilder withMetadataValue(String key, String metadataValue) {
        this.setMetadataValue(key, metadataValue);
        return this;
    }

    public DocumentBuilder withMetadataValue(String key, String[] metadataValue) {
        this.setMetadataValue(key, metadataValue);
        return this;
    }

    public DocumentBuilder withMetadataValue(String key, Integer metadataValue) {
        this.setMetadataValue(key, metadataValue);
        return this;
    }

    public DocumentBuilder withMetadataValue(String key, Integer[] metadataValue) {
        this.setMetadataValue(key, metadataValue);
        return this;
    }

    public DocumentBuilder withMetadata(Map<String, Object> metadata) {
        metadata.forEach(this::setMetadataValue);
        return this;
    }

    public DocumentBuilder withAllowedPermissions() {
        // TODO
        return this;
    }

    public DocumentBuilder withDeniedPermissions() {
        // TODO
        return this;
    }

    public String marshal() {
        JsonObject jsonDocument = new Gson().toJsonTree(this.document).getAsJsonObject();
        this.document.metadata.forEach((key, value) -> {
            jsonDocument.add(key, new Gson().toJsonTree(value));
        });
        jsonDocument.remove("metadata");
        return jsonDocument.toString();

    }

    private String dateFormat(DateTime dt) {
        return dt.toString(ISODateTimeFormat.dateTime());
    }

    private void setMetadataValue(String key, Object metadataValue) {
        this.validateReservedMetadataKeyNames(key);
        this.document.metadata.put(key, metadataValue);
    }

    private void validateCompressedBinaryData(String data) {
        // TODO
    }

    private void validateFileExtension(String fileExtension) {
        if (!fileExtension.startsWith(".")) {
            throw new RuntimeException(String.format("%s is not a valid file extension. It should start with a leading ."));
        }
    }

    private void validateReservedMetadataKeyNames(String key) {
        if (reservedKeynames.contains(key)) {
            throw new RuntimeException(String.format("Cannot use %s as a metadata key: It is a reserved keynames. See https://docs.coveo.com/en/78/index-content/push-api-reference#json-document-reserved-key-names", key));
        }
    }
}
