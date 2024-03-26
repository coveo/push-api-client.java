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
    if (this.fileContainer == null) {
      this.fileContainer = this.createFileContainer();
    }
    queue.add(document);
    return this.fileContainer;
  }

  public FileContainer addPartialUpdate(PartialUpdateDocument document)
      throws IOException, InterruptedException {
    if (this.fileContainer == null) {
      this.fileContainer = this.createFileContainer();
    }
    queue.add(document);
    return this.fileContainer;
  }

  public FileContainer delete(DeleteDocument document) throws IOException, InterruptedException {
    if (this.fileContainer == null) {
      this.fileContainer = this.createFileContainer();
    }
    queue.add(document);
    return this.fileContainer;
  }

  public HttpResponse<String> close()
      throws IOException, InterruptedException, NoOpenFileContainerException {
    return this.pushFileContainer(this.getSourceId());
  }

  private FileContainer createFileContainer() throws IOException, InterruptedException {
    this.logger.info("Creating new file container");
    HttpResponse<String> response = this.platformClient.createFileContainer();
    return new Gson().fromJson(response.body(), FileContainer.class);
  }

  private HttpResponse<String> pushFileContainer(String sourceId)
      throws NoOpenFileContainerException, IOException, InterruptedException {
    if (this.fileContainer == null) {
      throw new NoOpenFileContainerException(
          "No open file container detected. A new container will automatically be created once you start adding, updating or deleting documents.");
    }
    queue.flush();
    this.logger.info("Pushing to file container " + this.fileContainer.fileId);
    return this.platformClient.pushFileContainerContentToStreamSource(sourceId, this.fileContainer);
  }

  private String getSourceId() {
    return this.source.getId();
  }
}
