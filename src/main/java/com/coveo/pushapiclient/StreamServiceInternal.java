package com.coveo.pushapiclient;

import com.coveo.pushapiclient.exceptions.NoOpenStreamException;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.apache.logging.log4j.Logger;

/** For internal use only. Made to easily test the service without having to use PowerMock */
class StreamServiceInternal {
  private Logger logger;
  private final StreamEnabledSource source;
  private final PlatformClient platformClient;
  private String streamId;
  private DocumentUploadQueue queue;

  public StreamServiceInternal(
      StreamEnabledSource source,
      DocumentUploadQueue queue,
      PlatformClient platformClient,
      Logger logger) {
    this.source = source;
    this.queue = queue;
    this.platformClient = platformClient;
    this.logger = logger;
  }

  public String add(DocumentBuilder document) throws IOException, InterruptedException {
    if (this.streamId == null) {
      this.streamId = this.getStreamId();
    }
    queue.add(document);
    return this.streamId;
  }

  public HttpResponse<String> close()
      throws IOException, InterruptedException, NoOpenStreamException {
    if (this.streamId == null) {
      throw new NoOpenStreamException(
          "No open stream detected. A stream will automatically be opened once you start adding documents.");
    }
    queue.flush();
    String sourceId = this.getSourceId();
    this.logger.info("Closing open stream " + this.streamId);
    return this.platformClient.closeStream(sourceId, this.streamId);
  }

  private String getStreamId() throws IOException, InterruptedException {
    this.logger.info("Opening new stream");
    String sourceId = this.getSourceId();
    HttpResponse<String> response = this.platformClient.openStream(sourceId);
    StreamResponse streamResponse = new Gson().fromJson(response.body(), StreamResponse.class);
    return streamResponse.streamId;
  }

  private String getSourceId() {
    return this.source.getId();
  }
}
