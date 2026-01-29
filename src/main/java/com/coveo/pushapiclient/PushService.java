package com.coveo.pushapiclient;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpResponse;

public class PushService {
  private final PushEnabledSource source;
  private final PlatformClient platformClient;
  private PushServiceInternal service;

  public PushService(PushEnabledSource source) {
    this(source, new BackoffOptionsBuilder().build(), DocumentUploadQueue.getConfiguredBatchSize());
  }

  public PushService(PushEnabledSource source, BackoffOptions options) {
    this(source, options, DocumentUploadQueue.getConfiguredBatchSize());
  }

  /**
   * Creates a new PushService with configurable batch size.
   *
   * <p>Example batch sizes in bytes:
   * <ul>
   *   <li>5 MB (default): {@code 5 * 1024 * 1024} = {@code 5242880}
   *   <li>50 MB: {@code 50 * 1024 * 1024} = {@code 52428800}
   *   <li>256 MB (max): {@code 256 * 1024 * 1024} = {@code 268435456}
   * </ul>
   *
   * @param source The source to push documents to.
   * @param options The configuration options for exponential backoff.
   * @param maxQueueSize The maximum batch size in bytes before auto-flushing (default: 5MB, max:
   *     256MB).
   * @throws IllegalArgumentException if maxQueueSize exceeds 256MB or is not positive.
   */
  public PushService(PushEnabledSource source, BackoffOptions options, int maxQueueSize) {
    String apiKey = source.getApiKey();
    String organizationId = source.getOrganizationId();
    PlatformUrl platformUrl = source.getPlatformUrl();
    UploadStrategy uploader = this.getUploadStrategy();
    DocumentUploadQueue queue = new DocumentUploadQueue(uploader, maxQueueSize);

    this.platformClient = new PlatformClient(apiKey, organizationId, platformUrl, options);
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

  private UploadStrategy getUploadStrategy() {
    return (batchUpdate) -> {
      String sourceId = this.getSourceId();
      HttpResponse<String> resFileContainer = this.platformClient.createFileContainer();
      FileContainer fileContainer =
          new Gson().fromJson(resFileContainer.body(), FileContainer.class);
      this.platformClient.uploadContentToFileContainer(
          fileContainer, new Gson().toJson(batchUpdate.marshal()));
      return this.platformClient.pushFileContainerContent(sourceId, fileContainer);
    };
  }

  private String getSourceId() {
    return this.source.getId();
  }
}
