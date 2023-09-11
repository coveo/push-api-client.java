import com.coveo.pushapiclient.BackoffOptionsBuilder;
import com.coveo.pushapiclient.DocumentBuilder;
import com.coveo.pushapiclient.Source;

import java.io.IOException;
import java.net.http.HttpResponse;

public class PushOneDocument {
    public static void main(String[] args) {
        PushSource source = new PushSource("my_api_key", "my_org_id", new BackoffOptionsBuilder().withMaxRetries(5).withRetryAfter(10000).build());
        DocumentBuilder documentBuilder = new DocumentBuilder("https://my.document.uri", "My document title")
                .withData("these words will be searchable");

        try {
            HttpResponse<String> response = source.addOrUpdateDocument("my_source_id", documentBuilder);
            System.out.println(String.format("Push document status: %s", response.statusCode()));
            System.out.println(String.format("Push document response: %s", response.body()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
