import com.coveo.platform.Environment;
import com.coveo.platform.Region;
import com.coveo.platform.PlatformUrl;
import com.coveo.platform.PlatformUrlBuilder;
import com.coveo.source.SourceClient;
import com.coveo.source.SourceVisibility;

import java.io.IOException;
import java.net.http.HttpResponse;

public class CreateSource {
    public static void main(String[] args) {
        PlatformUrl platformUrl = new PlatformUrlBuilder()
                .withEnvironment(Environment.STAGING)
                .withRegion(Region.US) // If your organization is located in a different region than Region.US
                .build();
        SourceClient sourceClient = new SourceClient("my_api_key", "my_org_id", platformUrl);
        try {
            HttpResponse<String> response = sourceClient.create("pushy", SourceVisibility.SHARED);
            System.out.println(String.format("SourceClient creation status: %s", response.statusCode()));
            System.out.println(String.format("SourceClient creation response: %s", response.body()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
