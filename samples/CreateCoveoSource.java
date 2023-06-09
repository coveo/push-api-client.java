import com.coveo.pushapiclient.CatalogSource;
import com.coveo.pushapiclient.PlatformClient;
import com.coveo.pushapiclient.PushSource;
import com.coveo.pushapiclient.SourceVisibility;

import java.io.IOException;
import java.net.http.HttpResponse;



public class CreateCoveoSource {
    public static void main(String[] args) {
        PlatformClient platformClient = new PlatformClient("my_api_key", "my_org_id");
        try {
            HttpResponse<String> pushResponse = PushSource.create(platformClient, "the_name_of_my_source", SourceVisibility.SHARED);
            System.out.println(String.format("Push Source creation status: %s", pushResponse.statusCode()));
            System.out.println(String.format("Push Source creation response: %s", pushResponse.body()));

            HttpResponse<String> response = CatalogSource.create(platformClient, "the_name_of_my_source", SourceVisibility.SHARED);
            System.out.println(String.format("Catalog Source creation status: %s", response.statusCode()));
            System.out.println(String.format("Catalog Source creation response: %s", response.body()));

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
