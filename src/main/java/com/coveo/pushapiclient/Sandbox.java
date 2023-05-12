package com.coveo.pushapiclient;

import java.util.HashMap;
import java.util.List;

public class Sandbox {

    /**
     * Upload a document batch in push mode -> using the /files endpoint
     */
    public static void uploadBatch() {
        CatalogSource source = new CatalogSource("my_api_key", "my_org_id");

        // Prepare the push in "Update" mode. In this mode, the push Service will create
        // S3 file containers with appropriate batches sizes.
        DocumentUpdateService service = source.startDocumentUpdate("my_source_id");

        // Create a stream for pushing documents

        // Prepare a list of documents to add or update from an imaginary method
        // The SDK handles the batching. Simply feed documents into the service
        for (DocumentBuilder document : prepareDocuments()) {
            service.addOrUpdateDocument(document);
        }

        // Prepare a list of documents to partially update from an imaginary method
        // Same method can be used for both full and partial updates
        for (PartialUpdateDocument document : preparePartialUpdateDocuments()) {
            service.addOrUpdateDocument(document);
        }

        // Prepare a list of documents to delete from an imaginary method
        for (DeleteDocument document : prepareDocumentsToDelete()) {
            service.deleteDocument(document);
        }

        // Flush any previous documents buffered and not yet sent to the API.
        service.flush();
    }

    /**
     * Upload a document batch in stream mode -> using the /chunk endpoint
     */
    public static void streamBatch() {
        CatalogSource source = new CatalogSource("my_api_key", "my_org_id");

        // Prepare the push in "Stream" mode. In this mode, the push Service opens a
        // stream and creates the appropriates stream chunks
        FullCatalogUploadService service = source.startfullCatalogUpload("my_source_id");

        // Prepare a list of documents to add from an imaginary method
        // The SDK handles the batching.
        for (DocumentBuilder document : prepareDocuments()) {
            service.addDocument(document);
        }
        // Flush any previous documents buffered and not yet sent to the API.
        // Closes the stream
        service.flush();
    }

    /**
     * Push and delete documents individually.
     */
    public static void pushSingleDocument() {
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

        source.startDocumentUpdate("my_source_id")
                .addOrUpdateDocument(documentToAdd)
                .deleteDocument(documentToDelete)
                .flush();

    }

}
