package com.coveo.pushapiclient;

import com.coveo.pushapiclient.exceptions.NoOpenFileContainerException;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.apache.logging.log4j.Logger;

/** For internal use only. Made to easily test the service without having to use PowerMock */
class UpdateStreamServiceInternal {
  private final Logger logger;
  private final StreamEnabledSource source;
  private final PlatformClient platformClient;
  private final StreamDocumentUploadQueue queue;
  private FileContainer fileContainer;

  public UpdateStreamServiceInternal(
      final StreamEnabledSource source,
      final StreamDocumentUploadQueue queue,
      final PlatformClient platformClient,
      final Logger logger) {
    this.source = source;
    this.queue = queue;
    this.platformClient = platformClient;
    this.logger = logger;
  }

  public FileContainer addOrUpdate(DocumentBuilder document)
      throws IOException, InterruptedException {
    queue.add(document);
    return this.fileContainer;
  }

  public FileContainer addPartialUpdate(PartialUpdateDocument document)
      throws IOException, InterruptedException {
    queue.add(document);
    return this.fileContainer;
  }

  public FileContainer delete(DeleteDocument document) throws IOException, InterruptedException {
    queue.add(document);
    return this.fileContainer;
  }

  public HttpResponse<String> close()
      throws IOException, InterruptedException, NoOpenFileContainerException {
    HttpResponse<String> lastResponse = null;
    if (!queue.isEmpty()) {
      lastResponse = queue.flushAndPush();
    }
    return lastResponse;
  }

  // TODO: why is this unused
  private String getSourceId() {
    return this.source.getId();
  }
}
