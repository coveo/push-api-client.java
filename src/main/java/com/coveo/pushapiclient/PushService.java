package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.google.gson.Gson;

public class PushService {
    private final PushEnabledSource source;
    private final PlatformClient platformClient;
    private PushServiceInternal service;

    public PushService(PushEnabledSource source) {
        String apiKey = source.getApiKey();
        String organizationId = source.getOrganizationId();
        PlatformUrl platformUrl = source.getPlatformUrl();
        UpdloadStrategy uploader = this.getUploadStrategy();
        DocumentUploadQueue queue = new DocumentUploadQueue(uploader);

        this.platformClient = new PlatformClient(apiKey, organizationId, platformUrl);
        this.service = new PushServiceInternal(queue);
        this.source = source;
    }

    public void addOrUpdate(DocumentBuilder document) throws IOException, InterruptedException {
        // TODO: LENS-843: include partial document updates
        this.service.addOrUpdate(document);
    }

    public void delete(DeleteDocument document) throws IOException, InterruptedException {
        this.service.delete(document);
    }

    public void close() throws IOException, InterruptedException {
        this.service.close();
    }

    private UpdloadStrategy getUploadStrategy() {
        return (batchUpdate) -> {
            String sourceId = this.getSourceId();
            HttpResponse<String> resFileContainer = this.platformClient.createFileContainer();
            FileContainer fileContainer = new Gson().fromJson(resFileContainer.body(), FileContainer.class);
            this.platformClient.uploadContentToFileContainer(fileContainer, new Gson().toJson(batchUpdate.marshal()));
            return this.platformClient.pushFileContainerContent(sourceId, fileContainer);
        };
    }

    private String getSourceId() {
        return this.source.getId();
    }
}
