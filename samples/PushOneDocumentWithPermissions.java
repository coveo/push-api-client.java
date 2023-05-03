import com.coveo.document.DocumentBuilder;
import com.coveo.source.SourceClient;
import com.coveo.security.UserSecurityIdentityBuilder;

import java.io.IOException;
import java.net.http.HttpResponse;

public class PushOneDocumentWithPermissions {
    public static void main(String[] args) {
        SourceClient sourceClient = new SourceClient("my_api_key", "my_org_id");
        UserSecurityIdentityBuilder allowedUsers = new UserSecurityIdentityBuilder(new String[]{"bob@sample.com", "john@sample.com"});
        UserSecurityIdentityBuilder deniedUsers = new UserSecurityIdentityBuilder(new String[]{"jane@sample.com", "jack@sample.com"});

        DocumentBuilder documentBuilder = new DocumentBuilder("https://my.document.uri", "My document title")
                .withAllowAnonymousUsers(false)
                .withAllowedPermissions(allowedUsers)
                .withDeniedPermissions(deniedUsers);

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
