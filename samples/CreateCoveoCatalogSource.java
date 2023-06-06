import com.coveo.pushapiclient.*;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.coveo.pushapiclient.CatalogSource;

public class CreateCoveoCatalogSource {
    public static void main(String[] args) {
        PlatformClient platformClient = new PlatformClient("my_api_key", "my_org_id");
        try {
            HttpResponse<String> response = CatalogSource.create(platformClient, "the_name_of_my_source", SourceVisibility.SHARED);
            System.out.println(String.format("Catalog Source creation status: %s", response.statusCode()));
            System.out.println(String.format("Catalog Source creation response: %s", response.body()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
