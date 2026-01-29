import com.coveo.pushapiclient.*;

import java.io.IOException;

/**
 * Demonstrates how to configure the batch size for document uploads.
 *
 * The batch size controls how much data accumulates before automatically
 * creating a file container and pushing to Coveo. Default is 5 MB, max is 256 MB.
 */
public class ConfigureBatchSize {

    public static void main(String[] args) throws IOException, InterruptedException {

        PlatformUrl platformUrl = new PlatformUrlBuilder()
                .withEnvironment(Environment.PRODUCTION)
                .withRegion(Region.US)
                .build();

        CatalogSource catalogSource = CatalogSource.fromPlatformUrl(
                "my_api_key", "my_org_id", "my_source_id", platformUrl);

        // Option 1: Use default batch size (5 MB)
        UpdateStreamService defaultService = new UpdateStreamService(catalogSource);

        // Option 2: Configure batch size via constructor (50 MB)
        int fiftyMegabytes = 50 * 1024 * 1024;
        UpdateStreamService customService = new UpdateStreamService(
                catalogSource,
                new BackoffOptionsBuilder().build(),
                null,
                fiftyMegabytes);

        // Option 3: Configure globally via system property (affects all services)
        // Run with: java -Dcoveo.push.batchSize=52428800 ConfigureBatchSize
        // This sets 50 MB for all service instances that don't specify a size

        // Use the service
        DocumentBuilder document = new DocumentBuilder("https://my.document.uri", "My document title")
                .withData("these words will be searchable");

        customService.addOrUpdate(document);
        customService.close();
    }
}
