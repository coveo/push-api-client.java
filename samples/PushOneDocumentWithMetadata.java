import com.coveo.document.DocumentBuilder;
import com.coveo.source.SourceClient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class PushOneDocumentWithMetadata {
    public static void main(String[] args) {
        SourceClient sourceClient = new SourceClient("my_api_key", "my_org_id");
        DocumentBuilder documentBuilder = new DocumentBuilder("https://my.document.uri", "My document title")
                .withData("these words will be searchable")
                .withAuthor("bob")
                .withClickableUri("https://my.document.click.com")
                .withFileExtension(".html")
                .withMetadata(new HashMap<>() {{
                    put("tags", new String[]{"the_first_tag", "the_second_tag"});
                    put("version", 1);
                    put("somekey", "somevalue");
                }});

        try {
            HttpResponse<String> response = sourceClient.addOrUpdateDocument("my_source_id", documentBuilder);
            System.out.println(String.format("Push document status: %s", response.statusCode()));
            System.out.println(String.format("Push document response: %s", response.body()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
