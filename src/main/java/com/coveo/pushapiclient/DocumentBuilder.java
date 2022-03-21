package com.coveo.pushapiclient;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Utility class to build a {@link Document}
 */
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

    /**
     * @param uri   the URI of the document. See {@link Document#uri}
     * @param title the title of the document. See {@link Document#title}
     */
    public DocumentBuilder(String uri, String title) {
        this.document = new Document();
        this.document.uri = uri;
        this.document.title = title;
    }

    public Document getDocument() {
        return this.document;
    }

    /**
     * Set the data of the document. See {@link Document#data}
     *
     * @param data
     * @return
     */
    public DocumentBuilder withData(String data) {
        this.document.data = data;
        return this;
    }

    /**
     * Set the date of the document. See {@link Document#date}
     *
     * @param date
     * @return
     */
    public DocumentBuilder withDate(String date) {
        DateTime dt = DateTime.parse(date);
        this.document.date = this.dateFormat(dt);
        return this;
    }

    /**
     * Set the date of the document. See {@link Document#date}
     *
     * @param date
     * @return
     */
    public DocumentBuilder withDate(Long date) {
        DateTime dt = new DateTime(date);
        this.document.date = this.dateFormat(dt);
        return this;
    }

    /**
     * Set the date of the document. See {@link Document#date}
     *
     * @param date
     * @return
     */
    public DocumentBuilder withDate(Date date) {
        DateTime dt = new DateTime(date);
        this.document.date = this.dateFormat(dt);
        return this;
    }

    /**
     * Set the date of the document. See {@link Document#date}
     *
     * @param date
     * @return
     */
    public DocumentBuilder withDate(DateTime date) {
        this.document.date = this.dateFormat(date);
        return this;
    }

    /**
     * Set the modified date of the document. See {@link Document#modifiedDate}
     *
     * @param date
     * @return
     */
    public DocumentBuilder withModifiedDate(String date) {
        DateTime dt = DateTime.parse(date);
        this.document.modifiedDate = this.dateFormat(dt);
        return this;
    }

    /**
     * Set the modified date of the document. See {@link Document#modifiedDate}
     *
     * @param date
     * @return
     */
    public DocumentBuilder withModifiedDate(Long date) {
        DateTime dt = new DateTime(date);
        this.document.modifiedDate = this.dateFormat(dt);
        return this;
    }

    /**
     * Set the modified date of the document. See {@link Document#modifiedDate}
     *
     * @param date
     * @return
     */
    public DocumentBuilder withModifiedDate(DateTime date) {
        this.document.modifiedDate = this.dateFormat(date);
        return this;
    }

    /**
     * Set the modified date of the document. See {@link Document#modifiedDate}
     *
     * @param date
     * @return
     */
    public DocumentBuilder withModifiedDate(Date date) {
        DateTime dt = new DateTime(date);
        this.document.modifiedDate = this.dateFormat(dt);
        return this;
    }

    /**
     * Set the permanentID of the document. See {@link Document#permanentId}
     *
     * @param permanentId
     * @return
     */
    public DocumentBuilder withPermanentId(String permanentId) {
        this.document.permanentId = permanentId;
        return this;
    }

    /**
     * Set the base64 encoded, compressed binary data of the document. See {@link Document#compressedBinaryData}
     *
     * @param compressedBinaryData
     * @return
     */
    public DocumentBuilder withCompressedBinaryData(CompressedBinaryData compressedBinaryData) {
        this.document.compressedBinaryData = compressedBinaryData;
        return this;
    }

    /**
     * Set the file extension on the document. See {@link Document#fileExtension}
     *
     * @param fileExtension
     * @return
     */
    public DocumentBuilder withFileExtension(String fileExtension) {
        this.validateFileExtension(fileExtension);
        this.document.fileExtension = fileExtension;
        return this;
    }

    /**
     * Set the parentID on the document. See {@link Document#parentId}
     *
     * @param parentID
     * @return
     */
    public DocumentBuilder withParentID(String parentID) {
        this.document.parentId = parentID;
        return this;
    }

    /**
     * Set the clickableURI on the document. See {@link Document#clickableUri}
     *
     * @param clickableUri
     * @return
     */
    public DocumentBuilder withClickableUri(String clickableUri) {
        this.document.clickableUri = clickableUri;
        return this;
    }

    /**
     * Set the author on the document. See {@link Document#author}
     *
     * @param author
     * @return
     */
    public DocumentBuilder withAuthor(String author) {
        this.document.author = author;
        return this;
    }

    /**
     * Add a single metadata key and value pair on the document. See {@link Document#metadata}
     *
     * @param key
     * @param metadataValue
     * @return
     */
    public DocumentBuilder withMetadataValue(String key, String metadataValue) {
        this.setMetadataValue(key, metadataValue);
        return this;
    }

    /**
     * Add a single metadata key and value pair on the document. See {@link Document#metadata}
     *
     * @param key
     * @param metadataValue
     * @return
     */
    public DocumentBuilder withMetadataValue(String key, String[] metadataValue) {
        this.setMetadataValue(key, metadataValue);
        return this;
    }

    /**
     * Add a single metadata key and value pair on the document. See {@link Document#metadata}
     *
     * @param key
     * @param metadataValue
     * @return
     */
    public DocumentBuilder withMetadataValue(String key, Integer metadataValue) {
        this.setMetadataValue(key, metadataValue);
        return this;
    }

    /**
     * Add a single metadata key and value pair on the document. See {@link Document#metadata}
     *
     * @param key
     * @param metadataValue
     * @return
     */
    public DocumentBuilder withMetadataValue(String key, Integer[] metadataValue) {
        this.setMetadataValue(key, metadataValue);
        return this;
    }

    /**
     * Set metadata on the document. See {@link Document#metadata}
     *
     * @param metadata
     * @return
     */
    public DocumentBuilder withMetadata(Map<String, Object> metadata) {
        metadata.forEach(this::setMetadataValue);
        return this;
    }

    /**
     * Set allowed identities on the document. See {@link Document#permissions}
     *
     * @param allowedPermissions
     * @return
     */
    public DocumentBuilder withAllowedPermissions(SecurityIdentityBuilder allowedPermissions) {
        this.document.permissions[0].allowedPermissions = allowedPermissions.build();
        return this;
    }

    /**
     * Set denied identities on the document. See {@link Document#permissions}
     *
     * @param deniedPermissions
     * @return
     */
    public DocumentBuilder withDeniedPermissions(SecurityIdentityBuilder deniedPermissions) {
        this.document.permissions[0].deniedPermissions = deniedPermissions.build();
        return this;
    }

    /**
     * Set allowAnonymous for permissions on the document. See {@link Document#permissions}
     *
     * @param allowAnonymous
     * @return
     */
    public DocumentBuilder withAllowAnonymousUsers(Boolean allowAnonymous) {
        this.document.permissions[0].allowAnonymous = allowAnonymous;
        return this;
    }

    /**
     * Marshal the document into a JSON string accepted by the push API.
     *
     * @return
     */
    public String marshal() {
        return this.marshalJsonObject().toString();
    }

    /**
     * Marshal the document into a JSON object accepted by the push API.
     *
     * @return
     */
    public JsonObject marshalJsonObject() {
        this.generatePermanentId();

        JsonObject jsonDocument = new Gson().toJsonTree(this.document).getAsJsonObject();
        this.document.metadata.forEach((key, value) -> {
            jsonDocument.add(key, new Gson().toJsonTree(value));
        });
        jsonDocument.remove("metadata");

        if (this.document.compressedBinaryData != null) {
            jsonDocument.addProperty("compressedBinaryData", this.document.compressedBinaryData.getData());
        }

        jsonDocument.addProperty("documentId", this.document.uri);
        return jsonDocument;
    }

    private String dateFormat(DateTime dt) {
        return dt.toString(ISODateTimeFormat.dateTime());
    }

    private void setMetadataValue(String key, Object metadataValue) {
        this.validateReservedMetadataKeyNames(key);
        this.document.metadata.put(key, metadataValue);
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

    private void generatePermanentId() {
        if (this.document.permanentId == null) {
            String md5 = DigestUtils.md5Hex(this.document.uri);
            String sha1 = DigestUtils.sha1Hex(this.document.uri);
            this.document.permanentId = md5.substring(0, 30) + sha1.substring(0, 30);
        }
    }
}
