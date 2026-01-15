import com.coveo.pushapiclient.*;
import com.coveo.pushapiclient.exceptions.NoOpenStreamException;

import java.io.IOException;
import java.util.HashMap;

public class StreamDocuments {

    public static void main(String[] args) throws IOException, InterruptedException, NoOpenStreamException {

        PlatformUrl platformUrl = new PlatformUrlBuilder().withEnvironment(Environment.PRODUCTION).withRegion(Region.US).build();
        CatalogSource catalogSource = CatalogSource.fromPlatformUrl("my_api_key","my_org_id","my_source_id", platformUrl);

        // Using the Update Stream Service will act as an incremental change to the index, therefore any currently indexed items not contained in the payload will remain.
        UpdateStreamService updateStream = new UpdateStreamService(catalogSource);
        // To perform full index rebuild, use the StreamService instead.
        
        // Note: The UpdateStreamService now handles file containers differently for catalog sources.
        // Each batch (when the 256MB limit is exceeded or close() is called) will:
        // 1. Create a new file container
        // 2. Upload the batch content to that container
        // 3. Immediately push the container to the stream source via the /update API
        // This follows the catalog stream API best practices where each update operation uses its own file container.
        // You can configure a smaller batch size if needed by using the constructor with maxQueueSize parameter.

        DocumentBuilder document1 = new DocumentBuilder("https://my.document.uri", "My document title")
                .withData("these words will be searchable")
                .withAuthor("bob")
                .withClickableUri("https://my.document.click.com")
                .withFileExtension(".html")
                .withMetadata(new HashMap<>() {{
                    put("tags", new String[]{"the_first_tag", "the_second_tag"});
                    put("version", 1);
                    put("somekey", "somevalue");
                }});

        updateStreamService.addOrUpdate(document1);

        DocumentBuilder document2 = new DocumentBuilder("https://my.document2.uri", "My document2 title");
        updateStreamService.addOrUpdate(document2);

        DeleteDocument document3 = new DeleteDocument("https://my.document3.uri");
        updateStreamService.delete(document3);

        PartialUpdateDocument document4 = new PartialUpdateDocument("https://my.document4.uri", PartialUpdateOperator.FIELD_VALUE_REPLACE, "title", "My new title");
        updateStreamService.addPartialUpdate(document4);

        PartialUpdateDocument document5 = new PartialUpdateDocument("https://my.document5.uri", PartialUpdateOperator.DICTIONARY_PUT, "dictionaryAttribute", new HashMap<>() {{
            put("newkey", "newvalue");
        }});
        updateStreamService.addPartialUpdate(document5);

        PartialUpdateDocument document6 = new PartialUpdateDocument("https://my.document6.uri", PartialUpdateOperator.ARRAY_APPEND, "arrayAttribute", new String[]{"newValue"});
        updateStreamService.addPartialUpdate(document6);

        PartialUpdateDocument document7 = new PartialUpdateDocument("https://my.document7.uri", PartialUpdateOperator.ARRAY_REMOVE, "arrayAttribute", new String[]{"oldValue"});
        updateStreamService.addPartialUpdate(document7);

        PartialUpdateDocument document8 = new PartialUpdateDocument("https://my.document8.uri", PartialUpdateOperator.DICIONARY_REMOVE, "dictionaryAttribute", "oldkey");
        updateStreamService.addPartialUpdate(document8);

        updateStreamService.close();
    }
}
