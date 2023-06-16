package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpResponse;

public class CatalogSource implements StreamEnabledSource, PushEnabledSource {
  private final String apiKey;
  private final ApiUrl urlExtractor;

  /**
   * Creates a <a href='https://docs.coveo.com/en/3295'>Catalog Source</a> in Coveo Org
   *
   * @param platformClient
   * @param name The name of the source to create
   * @param sourceVisibility The security option that should be applied to the content of the
   *     source.
   * @see <a href="https://docs.coveo.com/en/1779">Content Security</a>
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public static HttpResponse<String> create(
      PlatformClient platformClient, String name, SourceVisibility sourceVisibility)
      throws IOException, InterruptedException {
    return platformClient.createSource(name, SourceType.CATALOG, sourceVisibility);
  }

  /**
   * Create a Catalog source instance from its <a
   * href="https://docs.coveo.com/en/3295#stream-api-url">Stream API URL</a>
   *
   * @param apiKey The API key used for all operations regarding your source.
   *     <p>Ensure your API key has the required privileges for the operation you will be performing
   *     *
   *     <p>For more information about which privileges are required, see <a href=
   *     "https://docs.coveo.com/en/1707#sources-domain">Privilege Reference.</a>
   * @param sourceUrl The URL available when you edit your source in the <a href=
   *     "https://docs.coveo.com/en/183/glossary/coveo-administration-console">Coveo Administration
   *     Console</a>. The URL should contain your <code>ORGANIZATION_ID</code> and <code>SOURCE_ID
   *     </code>, which are required parameters for all operations regarding your source.
   *     <p>Some examples of valid source URLs:
   *     <pre>
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
   * Create a Catalog source instance from its <a
   * href="https://docs.coveo.com/en/3295#stream-api-url">Stream API URL</a>
   *
   * @param apiKey The API key used for all operations regarding your source.
   *     <p>Ensure your API key has the required privileges for the operation you will be performing
   *     *
   *     <p>For more information about which privileges are required, see <a href=
   *     "https://docs.coveo.com/en/1707#sources-domain">Privilege Reference.</a>
   * @param organizationId The unique identifier of your organization.
   *     <p>The Organization Id can be retrieved in the URL of your Coveo organization.
   * @param sourceId The unique identifier of the target Catalog source.
   *     <p>The Source Id can be retrieved when you edit your source in the <a href=
   *     "https://docs.coveo.com/en/183/glossary/coveo-administration-console">Coveo Administration
   *     Console</a>
   */
  public static CatalogSource fromPlatformUrl(
      String apiKey, String organizationId, String sourceId) {
    PlatformUrl platformUrl =
        new PlatformUrl(PlatformUrl.DEFAULT_ENVIRONMENT, PlatformUrl.DEFAULT_REGION);
    return new CatalogSource(apiKey, organizationId, sourceId, platformUrl);
  }

  /**
   * Create a Catalog source instance from its <a
   * href="https://docs.coveo.com/en/3295#stream-api-url">Stream API URL</a>
   *
   * @param apiKey The API key used for all operations regarding your source.
   *     <p>Ensure your API key has the required privileges for the operation you will be performing
   *     *
   *     <p>For more information about which privileges are required, see <a href=
   *     "https://docs.coveo.com/en/1707#sources-domain">Privilege Reference.</a>
   * @param organizationId The unique identifier of your organization.
   *     <p>The Organization Id can be retrieved in the URL of your Coveo organization.
   * @param sourceId The unique identifier of the target Catalog source.
   *     <p>The Source Id can be retrieved when you edit your source in the <a href=
   *     "https://docs.coveo.com/en/183/glossary/coveo-administration-console">Coveo Administration
   *     Console</a>
   * @param platformUrl The object containing additional information on the URL endpoint. You can
   *     use the {@link PlatformUrl} when your organization is located in a non-default Coveo
   *     environement and/or region. When not specified, the default platform URL values will be
   *     used: {@link PlatformUrl#DEFAULT_ENVIRONMENT} and {@link PlatformUrl#DEFAULT_REGION}
   */
  public static CatalogSource fromPlatformUrl(
      String apiKey, String organizationId, String sourceId, PlatformUrl platformUrl) {
    return new CatalogSource(apiKey, organizationId, sourceId, platformUrl);
  }

  private CatalogSource(
      String apiKey, String organizationId, String sourceId, PlatformUrl platformUrl) {
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
