import com.coveo.pushapiclient.*;

import java.net.MalformedURLException;
import java.net.URL;

public class InstantiateSource {
    public static void main(String[] args) throws MalformedURLException {
            //create source from url
            URL url = new URL("https://api-eu.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents");
            PushSource pushSource1 = new PushSource("my_api_key", url);
            CatalogSource catalogSource1 = new CatalogSource("my_api_key", url);


            //create source from platform config
            PlatformUrl platformUrl = new PlatformUrlBuilder().withEnvironment(Environment.PRODUCTION).withRegion(Region.EU).build();
            PushSource pushSource2 = PushSource.fromPlatformUrl("my_api_key","my_organization_id","my_source_id", platformUrl);
            CatalogSource catalogSource2 = CatalogSource.fromPlatformUrl("my_api_key","my_organization_id","my_source_id", platformUrl);

    }
}
