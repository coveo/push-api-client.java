import com.coveo.pushapiclient.Source;
import com.coveo.pushapiclient.SourceVisibility;

import java.io.IOException;
import java.net.http.HttpResponse;

public class CreateSource {
    public static void main(String[] args) {
        // TODO: add example of how to provide different environments and regions
        Source source = new Source("my_api_key", "my_org_id");
        try {
            HttpResponse<String> response = source.create("the_name_of_my_source", SourceVisibility.SECURED);
            System.out.println(String.format("Source creation status: %s", response.statusCode()));
            System.out.println(String.format("Source creation response: %s", response.body()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
