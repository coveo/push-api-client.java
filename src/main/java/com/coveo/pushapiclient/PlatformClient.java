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

    public void createOrUpdateSecurityIdentity(String securityProviderId, Object securityIdentityModel) {
        // TODO
    }

    public void createOrUpdateSecurityIdentityAlias(String securityProviderId, Object securityIdentityAlias) {
        // TODO
    }

    public void deleteSecurityIdentity(String securityProviderId, Object securityIdentityToDelete) {
        // TODO
    }

    public void deleteOldSecurityIdentities(String securityProviderId, Object batchDelete) {
        // TODO
    }

    public void manageSecurityIdentities(String securityProviderId, Object batchConfig) {
        // TODO
    }

    public void pushDocument(String sourceId, Document doc) {
        // TODO
    }

    public void deleteDocument(String sourceId, String documentId, Boolean deleteChildren) {
        // TODO
    }

    public void createFileContainer() {
        // TODO
    }

    public void uploadContentToFileContainer(String sourceId, Object fileContainer) {
        // TODO
    }

    public void pushFileContainerContent(String sourceId, Object fileContainer) {
        // TODO
    }

    private String getBaseSourceURL() {
        return String.format("%s/sources", this.getBasePlatformURL());
    }

    private String getBasePlatformURL() {
        return String.format("https://platform.cloud.coveo.com/rest/organizations/%s", this.organizationId);
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
