import com.coveo.pushapiclient.*;
import com.coveo.pushapiclient.exceptions.NoOpenStreamException;

import java.io.IOException;
import java.util.HashMap;

public class StreamDocuments {

    public static void main(String[] args) throws IOException, InterruptedException, NoOpenStreamException {

        PlatformUrl platformUrl = new PlatformUrlBuilder().withEnvironment(Environment.PRODUCTION).withRegion(Region.US).build();
        CatalogSource catalogSource = CatalogSource.fromPlatformUrl("my_api_key","my_org_id","my_source_id", platformUrl);

        StreamService streamService = new StreamService(catalogSource);

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

        streamService.add(document1);

        DocumentBuilder document2 = new DocumentBuilder("https://my.document2.uri", "My document2 title");
        streamService.add(document2);

        streamService.close();
    }
}
