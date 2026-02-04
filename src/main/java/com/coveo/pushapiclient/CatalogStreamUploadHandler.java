package com.coveo.pushapiclient;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class CatalogStreamUploadHandler implements StreamUploadHandler {
  private static final Logger logger = LogManager.getLogger(CatalogStreamUploadHandler.class);
  private final StreamEnabledSource source;
  private final PlatformClient platformClient;

  CatalogStreamUploadHandler(StreamEnabledSource source, PlatformClient platformClient) {
    this.source = source;
    this.platformClient = platformClient;
  }

  @Override
  public HttpResponse<String> uploadAndPush(StreamUpdate stream)
      throws IOException, InterruptedException {
    // Step 1: Create file container
    logger.debug("Creating file container for stream upload");
    HttpResponse<String> containerResponse = platformClient.createFileContainer();
    FileContainer container = new Gson().fromJson(containerResponse.body(), FileContainer.class);

    // Step 2: Upload content to container
    String batchUpdateJson = new Gson().toJson(stream.marshal());
    logger.debug(
        "Uploading stream content to file container: {}", container.fileId);
    platformClient.uploadContentToFileContainer(container, batchUpdateJson);

    // Step 3: Push container to stream source
    logger.info("Pushing file container to stream source: {}", source.getId());
    return platformClient.pushFileContainerContentToStreamSource(source.getId(), container);
  }
}
