package com.coveo.pushapiclient;

import com.coveo.pushapiclient.exceptions.NoOpenFileContainerException;
import com.coveo.pushapiclient.exceptions.NoOpenStreamException;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.apache.logging.log4j.Logger;

/** For internal use only. Made to easily test the service without having to use PowerMock */
class StreamServiceInternal {
  private final Logger logger;
  private final StreamEnabledSource source;
  private final PlatformClient platformClient;
  private String streamId;
  private FileContainer fileContainer;
  private final DocumentUploadQueue queue;
  private final StreamOperationType operationType;

  public StreamServiceInternal(
      StreamEnabledSource source,
      DocumentUploadQueue queue,
      PlatformClient platformClient,
      StreamOperationType operationType,
      Logger logger) {
    this.source = source;
    this.queue = queue;
    this.platformClient = platformClient;
    this.operationType = operationType;
    this.logger = logger;
  }

  public String add(DocumentBuilder document) throws IOException, InterruptedException {
    if (StreamOperationType.INCREMENTAL.equals(getOperationType())) {
      throw new UnsupportedOperationException(
          "The incremental stream operation is not supported for the rebuild add method");
    }
    if (this.streamId == null) {
      this.streamId = this.openStream();
    }
    queue.add(document);
    return this.streamId;
  }

  public FileContainer addOrUpdate(DocumentBuilder document)
      throws IOException, InterruptedException {
    if (StreamOperationType.REBUILD.equals(getOperationType())) {
      throw new UnsupportedOperationException(
          "The full rebuild stream operation is not supported for the incramental addOrUpdate");
    }
    if (this.fileContainer == null) {
      this.fileContainer = this.createFileContainer();
    }
    queue.add(document);
    return this.fileContainer;
  }

  public FileContainer delete(DeleteDocument document) throws IOException, InterruptedException {
    if (StreamOperationType.REBUILD.equals(getOperationType())) {
      throw new UnsupportedOperationException(
          "The full rebuild stream operation is not supported for the incramental delete");
    }
    if (this.fileContainer == null) {
      this.fileContainer = this.createFileContainer();
    }
    queue.add(document);
    return this.fileContainer;
  }

  public HttpResponse<String> close()
      throws IOException,
          InterruptedException,
          NoOpenStreamException,
          NoOpenFileContainerException {
    if (StreamOperationType.REBUILD.equals(getOperationType())) {
      return this.closeStream(this.getSourceId());
    } else if (StreamOperationType.INCREMENTAL.equals(getOperationType())) {
      return this.pushFileContainer(this.getSourceId());
    } else {
      throw new UnsupportedOperationException(
          "Operation type not supported : " + getOperationType());
    }
  }

  private String openStream() throws IOException, InterruptedException {
    this.logger.info("Opening new stream");
    String sourceId = this.getSourceId();
    HttpResponse<String> response = this.platformClient.openStream(sourceId);
    StreamResponse streamResponse = new Gson().fromJson(response.body(), StreamResponse.class);
    return streamResponse.streamId;
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

  private HttpResponse<String> closeStream(String sourceId)
      throws IOException, InterruptedException, NoOpenStreamException {
    if (this.streamId == null) {
      throw new NoOpenStreamException(
          "No open stream detected. A stream will automatically be opened once you start adding documents.");
    }
    queue.flush();
    this.logger.info("Closing open stream " + this.streamId);
    return this.platformClient.closeStream(sourceId, this.streamId);
  }

  private String getSourceId() {
    return this.source.getId();
  }

  protected StreamOperationType getOperationType() {
    return this.operationType;
  }
}
