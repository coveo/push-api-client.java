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
    // Set this instance on the queue so it can call createUploadAndPush
    queue.setUpdateStreamService(this);
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

  /**
   * Creates a new file container, uploads the content, and pushes it to the stream source. This
   * method is called by the queue's flush operation to ensure each batch gets its own container.
   *
   * @param streamUpdate The batch of documents to upload
   * @return The HTTP response from pushing the file container
   * @throws IOException If an I/O error occurs
   * @throws InterruptedException If the operation is interrupted
   */
  public HttpResponse<String> createUploadAndPush(StreamUpdate streamUpdate)
      throws IOException, InterruptedException {
    // Step 1: Create a new file container
    this.logger.info("Creating new file container");
    HttpResponse<String> createResponse = this.platformClient.createFileContainer();
    FileContainer container = new Gson().fromJson(createResponse.body(), FileContainer.class);

    // Step 2: Upload content to the file container
    String batchUpdateJson = new Gson().toJson(streamUpdate.marshal());
    this.platformClient.uploadContentToFileContainer(container, batchUpdateJson);

    // Step 3: Push the file container to the stream source
    this.logger.info("Pushing file container " + container.fileId + " to stream source");
    return this.platformClient.pushFileContainerContentToStreamSource(
        this.getSourceId(), container);
  }

  private String getSourceId() {
    return this.source.getId();
  }
}
