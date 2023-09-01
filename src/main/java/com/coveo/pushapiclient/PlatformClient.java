package com.coveo.pushapiclient;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;

/** PlatformClient handles network requests to the Coveo platform */
public class PlatformClient {
  private final String apiKey;
  private final String organizationId;
  private final ApiCore api;
  private final PlatformUrl platformUrl;

  /**
   * Construct a PlatformClient
   *
   * @param apiKey An apiKey capable of pushing documents and managing sources in a Coveo
   *     organization.
   * @see <a href="https://docs.coveo.com/en/1718">Manage API Keys</a>
   * @param organizationId The Coveo Organization identifier.
   */
  public PlatformClient(String apiKey, String organizationId) {
    this(
        apiKey,
        organizationId,
        new PlatformUrlBuilder().build(),
        new BackoffOptionsBuilder().build());
  }

  /**
   * Construct a PlatformClient
   *
   * @param apiKey An apiKey capable of pushing documents and managing sources in a Coveo
   *     organization.
   * @see <a href="https://docs.coveo.com/en/1718">Manage API Keys</a>
   * @param organizationId The Coveo Organization identifier.
   */
  public PlatformClient(String apiKey, String organizationId, PlatformUrl platformUrl) {
    this(apiKey, organizationId, platformUrl, new BackoffOptionsBuilder().build());
  }

  /**
   * Construct a PlatformClient
   *
   * @param apiKey An apiKey capable of pushing documents and managing sources in a Coveo
   *     organization.
   * @see <a href="https://docs.coveo.com/en/1718">Manage API Keys</a>
   * @param organizationId The Coveo Organization identifier.
   * @param retryAfter The amount of time, in milliseconds, to wait between request attempts.
   * @param maxRetries The maximum number of attempts to make for a request.
   */
  public PlatformClient(String apiKey, String organizationId, BackoffOptions options) {
    this(apiKey, organizationId, new PlatformUrlBuilder().build(), options);
  }

  /**
   * Construct a PlatformClient
   *
   * @param apiKey An apiKey capable of pushing documents and managing sources in a Coveo
   *     organization.
   * @see <a href="https://docs.coveo.com/en/1718">Manage API Keys</a>
   * @param organizationId The Coveo Organization identifier.
   * @param platformUrl The PlatformUrl.
   * @param retryAfter The amount of time, in milliseconds, to wait between request attempts.
   * @param maxRetries The maximum number of attempts to make for a request.
   */
  public PlatformClient(
      String apiKey, String organizationId, PlatformUrl platformUrl, BackoffOptions options) {
    this.apiKey = apiKey;
    this.organizationId = organizationId;
    this.api = new ApiCore();
    this.platformUrl = platformUrl;
  }

  /**
   * Construct a PlatformClient
   *
   * @param apiKey An apiKey capable of pushing documents and managing sources in a Coveo
   *     organization.
   * @see <a href="https://docs.coveo.com/en/1718">Manage API Keys</a>
   * @param organizationId The Coveo Organization identifier.
   * @param httpClient The HttpClient.
   */
  public PlatformClient(String apiKey, String organizationId, HttpClient httpClient) {
    this(apiKey, organizationId, httpClient, new BackoffOptionsBuilder().build());
  }

  /**
   * Construct a PlatformClient
   *
   * @param apiKey An apiKey capable of pushing documents and managing sources in a Coveo
   *     organization.
   * @see <a href="https://docs.coveo.com/en/1718">Manage API Keys</a>
   * @param organizationId The Coveo Organization identifier.
   * @param httpClient The HttpClient.
   * @param retryAfter The amount of time, in milliseconds, to wait between request attempts.
   * @param maxRetries The maximum number of attempts to make for a request.
   */
  public PlatformClient(
      String apiKey, String organizationId, HttpClient httpClient, BackoffOptions options) {
    this.apiKey = apiKey;
    this.organizationId = organizationId;
    this.api = new ApiCore(httpClient, LogManager.getLogger(ApiCore.class), options);
    this.platformUrl = new PlatformUrlBuilder().build();
  }

  /**
   * @deprecated Please now use PlatformUrl to define your Platform environment
   * @see PlatformUrl Construct a PlatformUrl
   * @param apiKey An apiKey capable of pushing documents and managing sources in a Coveo
   *     organization.
   * @see <a href="https://docs.coveo.com/en/1718">Manage API Keys</a>
   * @param organizationId The Coveo Organization identifier.
   * @param environment The Environment to be used.
   */
  @Deprecated
  public PlatformClient(String apiKey, String organizationId, Environment environment) {
    this.apiKey = apiKey;
    this.organizationId = organizationId;
    this.api = new ApiCore();
    this.platformUrl = new PlatformUrlBuilder().withEnvironment(environment).build();
  }

  /**
   * Create a new push source
   *
   * @deprecated Please use {@link PlatformClient#createSource(String, SourceType,
   *     SourceVisibility)} instead
   * @param name
   * @param sourceVisibility
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  @Deprecated
  public HttpResponse<String> createSource(String name, SourceVisibility sourceVisibility)
      throws IOException, InterruptedException {
    return createSource(name, SourceType.PUSH, sourceVisibility);
  }

  /**
   * Create a new source
   *
   * @param name The name of the source to create
   * @param sourceType The type of the source to create
   * @param sourceVisibility The security option that should be applied to the content of the
   *     source.
   * @see <a href="https://docs.coveo.com/en/1779">Content Security</a>
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> createSource(
      String name, final SourceType sourceType, SourceVisibility sourceVisibility)
      throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());

    String json =
        this.toJSON(
            new HashMap<>() {
              {
                put("sourceType", sourceType.toString());
                put("pushEnabled", sourceType.isPushEnabled());
                put("streamEnabled", sourceType.isStreamEnabled());
                put("name", name);
                put("sourceVisibility", sourceVisibility);
              }
            });

    URI uri = URI.create(this.getBaseSourceURL());

    return this.api.post(uri, headers, HttpRequest.BodyPublishers.ofString(json));
  }

  /**
   * Create or update a security identity.
   *
   * @see <a href="https://docs.coveo.com/en/167">Adding a Single Security Identity</a>
   * @see <a href="https://docs.coveo.com/en/139">Security Identity Models</a>.
   * @param securityProviderId
   * @param securityIdentityModel
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> createOrUpdateSecurityIdentity(
      String securityProviderId, SecurityIdentityModel securityIdentityModel)
      throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
    URI uri = URI.create(this.getBaseProviderURL(securityProviderId) + "/permissions");

    String json = new Gson().toJson(securityIdentityModel);

    return this.api.put(uri, headers, HttpRequest.BodyPublishers.ofString(json));
  }

  /**
   * Create or update a security identity alias.
   *
   * @see <a href="https://docs.coveo.com/en/142">Adding a Single Alias</a>
   * @see <a href="https://docs.coveo.com/en/46">User Alias Definition Examples</a>
   * @param securityProviderId
   * @param securityIdentityAlias
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> createOrUpdateSecurityIdentityAlias(
      String securityProviderId, SecurityIdentityAliasModel securityIdentityAlias)
      throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
    URI uri = URI.create(this.getBaseProviderURL(securityProviderId) + "/mappings");

    String json = new Gson().toJson(securityIdentityAlias);

    return this.api.put(uri, headers, HttpRequest.BodyPublishers.ofString(json));
  }

  /**
   * Delete a security identity.
   *
   * @see <a href="https://docs.coveo.com/en/84">Disabling a Single Security Identity</a>
   * @param securityProviderId
   * @param securityIdentityToDelete
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> deleteSecurityIdentity(
      String securityProviderId, SecurityIdentityDelete securityIdentityToDelete)
      throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
    URI uri = URI.create(this.getBaseProviderURL(securityProviderId) + "/permissions");

    String json = new Gson().toJson(securityIdentityToDelete);

    return this.api.delete(uri, headers, HttpRequest.BodyPublishers.ofString(json));
  }

  /**
   * Delete old security identities.
   *
   * @see <a href="https://docs.coveo.com/en/33">Disabling Old Security Identities</a>
   * @param securityProviderId
   * @param batchDelete
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> deleteOldSecurityIdentities(
      String securityProviderId, SecurityIdentityDeleteOptions batchDelete)
      throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());

    URI uri =
        URI.create(
            this.getBaseProviderURL(securityProviderId)
                + String.format(
                    "/permissions/olderthan?queueDelay=%s%s",
                    batchDelete.getQueueDelay(), appendOrderingId(batchDelete.getOrderingId())));

    return this.api.delete(uri, headers);
  }

  /**
   * Returns the orderingId for the query string only when a valid orderingId is available.
   *
   * @param orderingId
   * @return
   */
  public String appendOrderingId(long orderingId) {
    if (orderingId > 0) {
      return String.format("&orderingId=%s", orderingId);
    }
    return "";
  }

  /**
   * Manage batches of security identities.
   *
   * @see <a href="https://docs.coveo.com/en/55">Manage Batches of Security Identities</a>
   * @param securityProviderId
   * @param batchConfig
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> manageSecurityIdentities(
      String securityProviderId, SecurityIdentityBatchConfig batchConfig)
      throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());

    URI uri =
        URI.create(
            this.getBaseProviderURL(securityProviderId)
                + String.format(
                    "/permissions/batch?fileId=%s%s",
                    batchConfig.getFileId(), appendOrderingId(batchConfig.getOrderingId())));

    return this.api.put(uri, headers, HttpRequest.BodyPublishers.noBody());
  }

  /**
   * Adds or updates an individual item in a push source.
   *
   * @see <a href="https://docs.coveo.com/en/133">Adding a Single Item in a Push Source</a>
   * @param sourceId
   * @param documentJSON
   * @param documentId
   * @param compressionType
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> pushDocument(
      String sourceId, String documentJSON, String documentId, CompressionType compressionType)
      throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());

    URI uri =
        URI.create(
            this.getBasePushURL()
                + String.format(
                    "/sources/%s/documents?documentId=%s&compressionType=%s",
                    sourceId, documentId, compressionType.toString()));

    return this.api.put(uri, headers, HttpRequest.BodyPublishers.ofString(documentJSON));
  }

  /**
   * Deletes a specific item from a Push source. Optionally, the child items of that item can also
   * be deleted.
   *
   * @see <a href="https://docs.coveo.com/en/171">Deleting an Item in a Push Source</a>
   * @param sourceId
   * @param documentId
   * @param deleteChildren
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> deleteDocument(
      String sourceId, String documentId, Boolean deleteChildren)
      throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());

    URI uri =
        URI.create(
            this.getBasePushURL()
                + String.format(
                    "/sources/%s/documents?documentId=%s&deleteChildren=%s",
                    sourceId, documentId, deleteChildren));

    return this.api.delete(uri, headers);
  }

  public HttpResponse<String> openStream(String sourceId) throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());

    URI uri =
        URI.create(this.getBasePushURL() + String.format("/sources/%s/stream/open", sourceId));

    return this.api.post(uri, headers);
  }

  public HttpResponse<String> closeStream(String sourceId, String streamId)
      throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
    URI uri =
        URI.create(
            this.getBasePushURL()
                + String.format("/sources/%s/stream/%s/close", sourceId, streamId));

    return this.api.post(uri, headers);
  }

  public HttpResponse<String> requireStreamChunk(String sourceId, String streamId)
      throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());

    URI uri =
        URI.create(
            this.getBasePushURL()
                + String.format("/sources/%s/stream/%s/chunk", sourceId, streamId));

    return this.api.post(uri, headers);
  }

  /**
   * Create a file container.
   *
   * @see <a href="https://docs.coveo.com/en/43">Creating a File Container</a>
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> createFileContainer() throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());

    URI uri = URI.create(this.getBasePushURL() + "/files");

    return this.api.post(uri, headers);
  }

  /**
   * Update the status of a Push source.
   *
   * @see <a href="https://docs.coveo.com/en/35">Updating the Status of a Push Source</a>
   * @param status
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> updateSourceStatus(String sourceId, PushAPIStatus status)
      throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());

    URI uri =
        URI.create(
            this.getBasePushURL()
                + String.format("/sources/%s/status?statusType=%s", sourceId, status.toString()));

    return this.api.post(uri, headers);
  }

  /**
   * Upload content update into a file container.
   *
   * @see <a
   *     href="https://docs.coveo.com/en/90/index-content/manage-batches-of-items-in-a-push-source#step-2-upload-the-content-update-into-the-file-container">Upload
   *     the Content Update into the File Container</a>
   * @param fileContainer
   * @param batchUpdateJson
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> uploadContentToFileContainer(
      FileContainer fileContainer, String batchUpdateJson)
      throws IOException, InterruptedException {
    String[] headers =
        fileContainer.requiredHeaders.entrySet().stream()
            .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
            .toArray(String[]::new);

    URI uri = URI.create(fileContainer.uploadUri);

    return this.api.put(uri, headers, HttpRequest.BodyPublishers.ofString(batchUpdateJson));
  }

  /**
   * Push a file container into a push source.
   *
   * @see <a
   *     href="https://docs.coveo.com/en/90/index-content/manage-batches-of-items-in-a-push-source#step-3-push-the-file-container-into-a-push-source">Push
   *     the File Container into a Push Source</a>
   * @param sourceId
   * @param fileContainer
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> pushFileContainerContent(String sourceId, FileContainer fileContainer)
      throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
    URI uri =
        URI.create(
            this.getBasePushURL()
                + String.format(
                    "/sources/%s/documents/batch?fileId=%s", sourceId, fileContainer.fileId));

    return this.api.put(uri, headers, HttpRequest.BodyPublishers.ofString(""));
  }

  /**
   * Push a file container into a stream source. See [Push the File Container into a Stream
   * Source](https://docs.coveo.com/en/l62e0540/coveo-for-commerce/how-to-update-your-catalog#step-3-send-the-file-container-to-update-your-catalog).
   *
   * @param sourceId
   * @param fileContainer
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> pushFileContainerContentToStreamSource(
      String sourceId, FileContainer fileContainer) throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
    URI uri =
        URI.create(
            this.getBasePushURL()
                + String.format(
                    "/sources/%s/stream/update?fileId=%s", sourceId, fileContainer.fileId));

    return this.api.put(uri, headers, HttpRequest.BodyPublishers.ofString(""));
  }

  /**
   * Push a binary to a File Container.
   *
   * @see <a
   *     href="https://docs.coveo.com/en/69#step-2-upload-the-item-data-into-the-file-container">Upload
   *     the Item Data Into the File Container</a>
   * @param fileContainer
   * @param fileAsBytes
   * @return
   * @throws IOException
   * @throws InterruptedException
   */
  public HttpResponse<String> pushBinaryToFileContainer(
      FileContainer fileContainer, byte[] fileAsBytes) throws IOException, InterruptedException {
    String[] headers =
        this.getHeaders(this.getAes256Header(), this.getContentTypeApplicationOctetStreamHeader());

    URI uri = URI.create(fileContainer.uploadUri);

    return this.api.put(uri, headers, HttpRequest.BodyPublishers.ofByteArray(fileAsBytes));
  }

  private String getBaseSourceURL() {
    return String.format("%s/sources", this.getBasePlatformURL());
  }

  private String getBasePlatformURL() {
    return String.format(
        "%s/rest/organizations/%s", this.platformUrl.getPlatformUrl(), this.organizationId);
  }

  private String getBasePushURL() {
    return String.format(
        "%s/push/v1/organizations/%s", this.platformUrl.getApiUrl(), this.organizationId);
  }

  private String getBaseProviderURL(String providerId) {
    return String.format("%s/providers/%s", this.getBasePushURL(), providerId);
  }

  private String[] getHeaders(String[]... headers) {
    String[] out = new String[] {};
    for (String[] header : headers) {
      out = Stream.concat(Arrays.stream(out), Arrays.stream(header)).toArray(String[]::new);
    }
    return out;
  }

  private String[] getAuthorizationHeader() {
    return new String[] {"Authorization", String.format("Bearer %s", this.apiKey)};
  }

  private String[] getContentTypeApplicationJSONHeader() {
    return new String[] {"Content-Type", "application/json", "Accept", "application/json"};
  }

  private String[] getAes256Header() {
    return new String[] {"x-amz-server-side-encryption", "AES256"};
  }

  private String[] getContentTypeApplicationOctetStreamHeader() {
    return new String[] {"Content-Type", "application/octet-stream"};
  }

  private String toJSON(HashMap<String, Object> hashMap) {
    return new Gson().toJson(hashMap, new TypeToken<HashMap<String, Object>>() {}.getType());
  }
}
