package com.coveo.pushapiclient;

import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Date;
import java.util.Map;

public class DocumentBuilder {
    private Document document;

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

    public DocumentBuilder withAllowedPermissions(SecurityIdentityBuilder allowedPermissions) {
        this.document.permissions.allowedPermissions = allowedPermissions.build();
        return this;
    }

    public DocumentBuilder withDeniedPermissions(SecurityIdentityBuilder deniedPermissions) {
        this.document.permissions.deniedPermissions = deniedPermissions.build();
        return this;
    }

    public String marshal() {
        return new Gson().toJson(this.document);
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
        // TODO
    }

    private void validateReservedMetadataKeyNames(String key) {
        // TODO
    }
}
