package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.http.HttpResponse;

public class Source {
    PlatformClient platformClient;

    public Source(String apiKey, String organizationId) {
        this.platformClient = new PlatformClient(apiKey, organizationId);
    }

    public HttpResponse<String> create(String name, SourceVisibility sourceVisibility) throws IOException, InterruptedException {
        return this.platformClient.createSource(name, sourceVisibility);
    }
}
