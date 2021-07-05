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

public class PlatformClient {
    private final String apiKey;
    private final String organizationId;
    private final HttpClient httpClient;

    public PlatformClient(String apiKey, String organizationId) {
        this.apiKey = apiKey;
        this.organizationId = organizationId;
        this.httpClient = HttpClient.newHttpClient();
    }

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

    public HttpResponse<String> deleteOldSecurityIdentities(String securityProviderId, SecurityIdentityDeleteOptions batchDelete) throws IOException, InterruptedException {
        String[] headers = this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
        URI uri = URI.create(this.getBaseProviderURL(securityProviderId) + String.format("/permissions/olderthan?orderingId=%s&queueDelay=%s", batchDelete.orderingId, batchDelete.queueDelay));

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .DELETE()
                .uri(uri)
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> manageSecurityIdentities(String securityProviderId, SecurityIdentityBatchConfig batchConfig) throws IOException, InterruptedException {
        String[] headers = this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
        URI uri = URI.create(this.getBaseProviderURL(securityProviderId) + String.format("/permissions/batch?fileId=%s&orderingId=%s", batchConfig.fileId, batchConfig.orderingId));

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .uri(uri)
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> pushDocument(String sourceId, String documentJSON, String documentId) throws IOException, InterruptedException {
        String[] headers = this.getHeaders(this.getAuthorizationHeader(), this.getContentTypeApplicationJSONHeader());
        URI uri = URI.create(this.getBasePushURL() + String.format("/sources/%s/documents?documentId=%s", sourceId, documentId));

        HttpRequest request = HttpRequest.newBuilder()
                .headers(headers)
                .PUT(HttpRequest.BodyPublishers.ofString(documentJSON))
                .uri(uri)
                .build();

        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

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
