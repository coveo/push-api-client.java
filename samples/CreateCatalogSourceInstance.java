import com.coveo.pushapiclient.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

public class CreateCatalogSourceInstance {
    public static void main(String[] args) throws FileNotFoundException {
        Properties properties = new Properties();
        try {
            //create source from url
            URL url = new URL("my_api_url");
            CatalogSource catalogSource1 = new CatalogSource("my_api_key", url);

            //create source from platform config
            PlatformUrl platformUrl = new PlatformUrlBuilder().withEnvironment(Environment.PRODUCTION).withRegion(Region.EU).build();
            CatalogSource catalogSource2 = CatalogSource.fromPlatformUrl("my_api_key","my_organization_id","my_source_id", platformUrl);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
