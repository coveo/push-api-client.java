package com.coveo.pushapiclient;

import java.util.HashMap;
import java.util.List;

public class Sandbox {

    /**
     * Upload a document batch in push mode -> using the /files endpoint
     */
    public static void uploadBatch() {
        CatalogSource source = new CatalogSource("source_url", "my_api_key");

        // Prepare the push in "Update" mode by creating the appropriate upload service.
        // In this mode, the push Service will create S3 file containers with
        // appropriate batches sizes.
        try (PushService stream = new PushService(source)) {

            // Prepare a list of documents to add or update from an imaginary method
            // The SDK handles the batching. Simply feed documents into the service
            for (DocumentBuilder document : prepareDocuments()) {
                stream.addOrUpdate(document);
            }

            // Prepare a list of documents to partially update from an imaginary method
            // Same method can be used for both full and partial updates
            for (PartialUpdateDocument document : preparePartialUpdateDocuments()) {
                stream.partialUpdate(document);
            }

            // Prepare a list of documents to delete from an imaginary method
            for (DeleteDocument document : prepareDocumentsToDelete()) {
                stream.delete(document);
            }
        }
    }

    /**
     * Upload a document batch in stream mode -> using the /chunk endpoint
     */
    public static void streamBatch() {
        // Instanciate a stream source in "Stream" mode. In this mode, the push Service
        // opens a stream and creates the appropriates stream chunks
        CatalogSource source = new CatalogSource("source_url", "my_api_key");
        try (StreamService stream = new StreamService(source)) {
            // Prepare a list of documents to add from an imaginary method
            // The SDK handles the batching.
            for (DocumentBuilder document : prepareDocuments()) {
                stream.add(document);
            }
        }
    }

    /**
     * Push and delete documents individually.
     * 
     * @throws Exception
     */
    public static void pushSingleDocument() throws Exception {
        CatalogSource source = new CatalogSource("my_api_key", "my_org_id");
        DocumentBuilder documentToAdd = new DocumentBuilder("https://my.document.uri", "My document title")
                .withData("these words will be searchable")
                .withAuthor("bob")
                .withClickableUri("https://my.document.click.com")
                .withFileExtension(".html")
                .withMetadata(new HashMap<>() {
                    {
                        put("tags", new String[] { "the_first_tag", "the_second_tag" });
                        put("version", 1);
                        put("somekey", "somevalue");
                    }
                });

        DeleteDocument documentToDelete = new DeleteDocument("https:/document.todelete.uri");

        PushService stream = new PushService(source);
        stream.addOrUpdate(documentToAdd);
        stream.delete(documentToDelete);
        stream.close();

    }

}
