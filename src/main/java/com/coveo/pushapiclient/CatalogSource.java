package com.coveo.pushapiclient;

import java.net.MalformedURLException;
import java.net.URL;

// TODO: LENS-851 - Make public when ready
class CatalogSource implements StreamEnabledSource {
    private final PlatformClient platformClient;
    private final String sourceId;

    /**
     * Create a Catalog source instance from its
     * <a href="https://docs.coveo.com/en/3295#stream-api-url">Stream API URL</a>
     *
     * @param apiKey    The API key used for all operations regarding your source.
     *                  <p>
     *                  Ensure your API key has the required privileges for the
     *                  operation you will be performing
     *                  *
     *                  <p>
     *                  For more information about which privileges are required,
     *                  see
     *                  <a href=
     *                  "https://docs.coveo.com/en/1707#sources-domain">Privilege
     *                  Reference.</a>
     *
     * @param sourceUrl The URL available when you edit your source in the <a href=
     *                  "https://docs.coveo.com/en/183/glossary/coveo-administration-console">Coveo
     *                  Administration Console</a>. The URL should contain your
     *                  <code>ORGANIZATION_ID</code> and <code>SOURCE_ID</code>,
     *                  which are required parameters for all operations regarding
     *                  your source.
     *                  <p>
     *                  Some examples of valid source URLs:
     *
     *                  <pre>
     * https://api.cloud.coveo.com/push/v1/organizations/my-org-if/sources/my-source-id/stream/open
     * https://api-eu.cloud.coveo.com/push/v1/organizations/my-org-if/sources/my-source-id/stream/open
     *                  </pre>
     *
     * @throws MalformedURLException
     */
    public CatalogSource(String apiKey, URL sourceUrl) throws MalformedURLException {
        ApiUrl parser = new ApiUrl(sourceUrl);
        PlatformUrl platformUrl = parser.getPlatformUrl();
        String organizationId = parser.getOrganizationId();
        this.sourceId = parser.getSourceId();
        this.platformClient = new PlatformClient(apiKey, organizationId,
                platformUrl);
    }

    /**
     * Create a Catalog source instance from its
     * <a href="https://docs.coveo.com/en/3295#stream-api-url">Stream API URL</a>
     *
     * @param apiKey         The API key used for all operations regarding your
     *                       source.
     *                       <p>
     *                       Ensure your API key has the required privileges for the
     *                       operation you will be performing
     *                       *
     *                       <p>
     *                       For more information about which privileges are
     *                       required,
     *                       see
     *                       <a href=
     *                       "https://docs.coveo.com/en/1707#sources-domain">Privilege
     *                       Reference.</a>
     *
     * @param organizationId The unique identifier of your organization.
     *                       <p>
     *                       The Organization Id can be retrieved in the URL of your
     *                       Coveo organization.
     *
     * @param sourceId       The unique identifier of the target Catalog source.
     *                       <p>
     *                       The Source Id can be retrieved when you edit your
     *                       source in the <a href=
     *                       "https://docs.coveo.com/en/183/glossary/coveo-administration-console">Coveo
     *                       Administration Console</a>
     *
     */
    public CatalogSource(String apiKey, String organizationId, String sourceId) {
        PlatformUrl platformUrl = new PlatformUrl(PlatformUrl.DEFAULT_ENVIRONMENT, PlatformUrl.DEFAULT_REGION);
        this.sourceId = sourceId;
        this.platformClient = new PlatformClient(apiKey, organizationId,
                platformUrl);
    }

    /**
     * Create a Catalog source instance from its
     * <a href="https://docs.coveo.com/en/3295#stream-api-url">Stream API URL</a>
     *
     * @param apiKey         The API key used for all operations regarding your
     *                       source.
     *                       <p>
     *                       Ensure your API key has the required privileges for the
     *                       operation you will be performing
     *                       *
     *                       <p>
     *                       For more information about which privileges are
     *                       required,
     *                       see
     *                       <a href=
     *                       "https://docs.coveo.com/en/1707#sources-domain">Privilege
     *                       Reference.</a>
     *
     * @param organizationId The unique identifier of your organization.
     *                       <p>
     *                       The Organization Id can be retrieved in the URL of your
     *                       Coveo organization.
     *
     * @param sourceId       The unique identifier of the target Catalog source.
     *                       <p>
     *                       The Source Id can be retrieved when you edit your
     *                       source in the <a href=
     *                       "https://docs.coveo.com/en/183/glossary/coveo-administration-console">Coveo
     *                       Administration Console</a>
     *
     * @param platformUrl    The object containing additional information on the
     *                       URL endpoint.
     *                       You can use the {@link PlatformUrl} when your
     *                       organization is located in a non-default Coveo
     *                       environement and/or region.
     *
     */
    public CatalogSource(String apiKey, String organizationId, String sourceId, PlatformUrl platformUrl) {
        this.sourceId = sourceId;
        this.platformClient = new PlatformClient(apiKey, organizationId,
                platformUrl);
    }

    @Override
    public String getId() {
        return this.sourceId;
    }

    @Override
    public PlatformClient getPlatformClient() {
        return this.platformClient;
    }

}
