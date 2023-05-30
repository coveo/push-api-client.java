package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

// TODO: LENS-851 - Make public when ready
class CatalogSource implements StreamEnabledSource {
    private final String apiKey;
    private final ApiUrl urlExtractor;


    /**
     * Creates a Catalog Source in Coveo Org
     *
     * @param platformUrl
     * @param organizationId
     * @param apiKey
     * @param name
     * @param sourceVisibility
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static void create(PlatformUrl platformUrl, String organizationId, String apiKey, String name, SourceVisibility sourceVisibility) throws IOException, InterruptedException {
        new PlatformClient(apiKey,organizationId,platformUrl).createSource(name, SourceType.CATALOG.name(), true, true, sourceVisibility);
    }

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
        this.apiKey = apiKey;
        this.urlExtractor = new ApiUrl(sourceUrl);
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
    public static CatalogSource fromPlatformUrl(String apiKey, String organizationId, String sourceId) {
        PlatformUrl platformUrl = new PlatformUrl(PlatformUrl.DEFAULT_ENVIRONMENT, PlatformUrl.DEFAULT_REGION);
        return new CatalogSource(apiKey, organizationId, sourceId, platformUrl);
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
     *                       environement and/or region. When not specified, the
     *                       default platform URL values will be used:
     *                       {@link PlatformUrl#DEFAULT_ENVIRONMENT} and
     *                       {@link PlatformUrl#DEFAULT_REGION}
     *
     */
    public static CatalogSource fromPlatformUrl(String apiKey, String organizationId, String sourceId,
            PlatformUrl platformUrl) {
        return new CatalogSource(apiKey, organizationId, sourceId, platformUrl);
    }

    private CatalogSource(String apiKey, String organizationId, String sourceId, PlatformUrl platformUrl) {
        this.apiKey = apiKey;
        this.urlExtractor = new ApiUrl(organizationId, sourceId, platformUrl);
    }

    @Override
    public String getOrganizationId() {
        return this.urlExtractor.getOrganizationId();
    }

    @Override
    public PlatformUrl getPlatformUrl() {
        return this.urlExtractor.getPlatformUrl();
    }

    @Override
    public String getId() {
        return this.urlExtractor.getSourceId();
    }

    @Override
    public String getApiKey() {
        return this.apiKey;
    }

}
