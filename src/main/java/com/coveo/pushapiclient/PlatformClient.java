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

/**
 * PlatformClient handles network requests to the Coveo platform
 */
public class PlatformClient {
    private final String apiKey;
    private final String organizationId;
    private final HttpClient httpClient;

    /**
     * Construct a PlatformClient
     *
     * @param apiKey         An apiKey capable of pushing documents and managing sources in a Coveo organization. See [Manage API Keys](https://docs.coveo.com/en/1718).
     * @param organizationId The Coveo Organization identifier.
     */
    public PlatformClient(String apiKey, String organizationId) {
        this.apiKey = apiKey;
        this.organizationId = organizationId;
        this.httpClient = HttpClient.newHttpClient();
    }

    public PlatformClient(String apiKey, String organizationId, HttpClient httpClient) {
        this.apiKey = apiKey;
        this.organizationId = organizationId;
        this.httpClient = httpClient;
    }

    /**
     * Create a new push source
     *
     * @param name             The name of the source to create
     * @param sourceVisibility The security option that should be applied to the content of the source. See [Content Security](https://docs.coveo.com/en/1779).
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> createSource(String name, SourceVisibility sourceVisibility) throws IOException, InterruptedException {
        String[] headers = this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());

        String json = this.toJSON(new HashMap<>() {{
            put("sourceType", "PUSH");
            put("pushEnabled", true);
            put("name", name);
            put("sourceVisibility", sourceVisibility);
        }});

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(this.getBaseSourceURL()))
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Create or update a security identity. See [Adding a Single Security Identity](https://docs.coveo.com/en/167) and [Security Identity Models](https://docs.coveo.com/en/139).
     *
     * @param securityProviderId
     * @param securityIdentityModel
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> createOrUpdateSecurityIdentity(String securityProviderId, SecurityIdentityModel securityIdentityModel) throws IOException, InterruptedException {
        String[] headers = this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
        URI uri = URI.create(this.getBaseProviderURL(securityProviderId) + "/permissions");

        String json = new Gson().toJson(securityIdentityModel);

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Create or update a security identity alias. See [Adding a Single Alias](https://docs.coveo.com/en/142) and [User Alias Definition Examples](https://docs.coveo.com/en/46).
     *
     * @param securityProviderId
     * @param securityIdentityAlias
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> createOrUpdateSecurityIdentityAlias(String securityProviderId, SecurityIdentityAliasModel securityIdentityAlias) throws IOException, InterruptedException {
        String[] headers = this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
        URI uri = URI.create(this.getBaseProviderURL(securityProviderId) + "/mappings");

        String json = new Gson().toJson(securityIdentityAlias);

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Delete a security identity. See [Disabling a Single Security Identity](https://docs.coveo.com/en/84).
     *
     * @param securityProviderId
     * @param securityIdentityToDelete
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> deleteSecurityIdentity(String securityProviderId, SecurityIdentityDelete securityIdentityToDelete) throws IOException, InterruptedException {
        String[] headers = this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
        URI uri = URI.create(this.getBaseProviderURL(securityProviderId) + "/permissions");

        String json = new Gson().toJson(securityIdentityToDelete);

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .method("DELETE", HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Delete old security identities. See [Disabling Old Security Identities](https://docs.coveo.com/en/33).
     *
     * @param securityProviderId
     * @param batchDelete
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> deleteOldSecurityIdentities(String securityProviderId, SecurityIdentityDeleteOptions batchDelete) throws IOException, InterruptedException {
        String[] headers = this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
        URI uri = URI.create(this.getBaseProviderURL(securityProviderId) + String.format("/permissions/olderthan?orderingId=%s&queueDelay=%s", batchDelete.orderingId(), batchDelete.queueDelay()));

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .DELETE()
                .uri(uri)
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Manage batches of security identities. See [Manage Batches of Security Identities](https://docs.coveo.com/en/55).
     *
     * @param securityProviderId
     * @param batchConfig
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> manageSecurityIdentities(String securityProviderId, SecurityIdentityBatchConfig batchConfig) throws IOException, InterruptedException {
        String[] headers = this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
        URI uri = URI.create(this.getBaseProviderURL(securityProviderId) + String.format("/permissions/batch?fileId=%s&orderingId=%s", batchConfig.fileId(), batchConfig.orderingId()));

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .PUT(HttpRequest.BodyPublishers.noBody())
                .uri(uri)
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Adds or updates an individual item in a push source. See [Adding a Single Item in a Push Source](https://docs.coveo.com/en/133).
     *
     * @param sourceId
     * @param documentJSON
     * @param documentId
     * @param compressionType
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> pushDocument(String sourceId, String documentJSON, String documentId, CompressionType compressionType) throws IOException, InterruptedException {
        String[] headers = this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
        URI uri = URI.create(this.getBasePushURL() + String.format("/sources/%s/documents?documentId=%s&compressionType=%s", sourceId, documentId, compressionType.toString()));

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .PUT(HttpRequest.BodyPublishers.ofString(documentJSON))
                .uri(uri)
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Deletes a specific item from a Push source. Optionally, the child items of that item can also be deleted. See [Deleting an Item in a Push Source](https://docs.coveo.com/en/171).
     *
     * @param sourceId
     * @param documentId
     * @param deleteChildren
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> deleteDocument(String sourceId, String documentId, Boolean deleteChildren) throws IOException, InterruptedException {
        String[] headers = this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
        URI uri = URI.create(this.getBasePushURL() + String.format("/sources/%s/documents?documentId=%s&deleteChildren=%s", sourceId, documentId, deleteChildren));

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .DELETE()
                .uri(uri)
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Create a file container. See [Creating a File Container](https://docs.coveo.com/en/43).
     *
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> createFileContainer() throws IOException, InterruptedException {
        String[] headers = this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
        URI uri = URI.create(this.getBasePushURL() + "/files");

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Upload content update into a file container. See [Upload the Content Update into the File Container](https://docs.coveo.com/en/90/index-content/manage-batches-of-items-in-a-push-source#step-2-upload-the-content-update-into-the-file-container).
     *
     * @param sourceId
     * @param fileContainer
     * @param batchUpdate
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> uploadContentToFileContainer(String sourceId, FileContainer fileContainer, BatchUpdateRecord batchUpdate) throws IOException, InterruptedException {
        String[] headers = fileContainer.requiredHeaders.entrySet()
                .stream()
                .flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
                .toArray(String[]::new);
        URI uri = URI.create(fileContainer.uploadUri);


        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(new Gson().toJson(batchUpdate)))
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Push a file container into a push source. See [Push the File Container into a Push Source](https://docs.coveo.com/en/90/index-content/manage-batches-of-items-in-a-push-source#step-3-push-the-file-container-into-a-push-source).
     *
     * @param sourceId
     * @param fileContainer
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public HttpResponse<String> pushFileContainerContent(String sourceId, FileContainer fileContainer) throws IOException, InterruptedException {
        String[] headers = this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
        URI uri = URI.create(this.getBasePushURL() + String.format("/sources/%s/documents/batch?fileId=%s", sourceId, fileContainer.fileId));

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .uri(uri)
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String getBaseSourceURL() {
        return String.format("%s/sources", this.getBasePlatformURL());
    }

    private String getBasePlatformURL() {
        return String.format("https://platform.cloud.coveo.com/rest/organizations/%s", this.organizationId);
    }

    private String getBasePushURL() {
        return String.format("https://api.cloud.coveo.com/push/v1/organizations/%s", this.organizationId);
    }

    private String getBaseProviderURL(String providerId) {
        return String.format("%s/providers/%s", this.getBasePushURL(), providerId);
    }

    private String[] getHeaders(String[]... headers) {
        String[] out = new String[]{};
        for (String[] header : headers) {
            out = Stream.concat(Arrays.stream(out), Arrays.stream(header))
                    .toArray(String[]::new);
        }
        return out;
    }

    private String[] getAuthorizationHeader() {
        return new String[]{"Authorization", String.format("Bearer %s", this.apiKey)};
    }

    private String[] getContentTypeApplicationJSONHeader() {
        return new String[]{"Content-Type", "application/json", "Accept", "application/json"};
    }

    private String toJSON(HashMap<String, Object> hashMap) {
        return new Gson().toJson(hashMap, new TypeToken<HashMap<String, Object>>() {
        }.getType());
    }
}
