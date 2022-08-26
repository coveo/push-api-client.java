import com.coveo.pushapiclient.Region;
import com.coveo.pushapiclient.PlatformUrl;
import com.coveo.pushapiclient.PlatformUrlBuilder;
import com.coveo.pushapiclient.Source;
import com.coveo.pushapiclient.SourceVisibility;

import java.io.IOException;
import java.net.http.HttpResponse;

public class CreateSource {
    public static void main(String[] args) {
        PlatformUrl platformUrl = new PlatformUrlBuilder()
                .withRegion(Region.AU) // If your organization is located in a different region than Region.US
                .build();
        Source source = new Source("my_api_key", "my_org_id", platformUrl);
        try {
            HttpResponse<String> response = source.create("the_name_of_my_source", SourceVisibility.SECURED);
            System.out.println(String.format("Source creation status: %s", response.statusCode()));
            System.out.println(String.format("Source creation response: %s", response.body()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
