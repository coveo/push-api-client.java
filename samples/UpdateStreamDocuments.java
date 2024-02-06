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

        updateStreamService.close();
    }
}
