package com.coveo.pushapiclient;

import com.coveo.pushapiclient.exceptions.NoOpenStreamException;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpResponse;

/** For internal use only. Made to easily test the service without having to use PowerMock */
class StreamServiceInternal {
  private final StreamEnabledSource source;
  private final PlatformClient platformClient;
  private String streamId;
  private DocumentUploadQueue queue;

  public StreamServiceInternal(
      StreamEnabledSource source, DocumentUploadQueue queue, PlatformClient platformClient) {
    this.source = source;
    this.queue = queue;
    this.platformClient = platformClient;
  }

  public void add(DocumentBuilder document) throws IOException, InterruptedException {
    if (this.streamId == null) {
      this.streamId = this.getStreamId();
    }
    queue.add(document);
  }

  public HttpResponse<String> close()
      throws IOException, InterruptedException, NoOpenStreamException {
    if (this.streamId == null) {
      throw new NoOpenStreamException(
          "No open stream detected. A stream will automatically be opened once you start adding documents.");
    }
    queue.flush();
    String sourceId = this.getSourceId();
    return this.platformClient.closeStream(sourceId, this.streamId);
  }

  private String getStreamId() throws IOException, InterruptedException {
    String sourceId = this.getSourceId();
    HttpResponse<String> response = this.platformClient.openStream(sourceId);
    StreamResponse streamResponse = new Gson().fromJson(response.body(), StreamResponse.class);
    return streamResponse.streamId;
  }

  private String getSourceId() {
    return this.source.getId();
  }
}
