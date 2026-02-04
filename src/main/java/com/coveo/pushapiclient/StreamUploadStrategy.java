package com.coveo.pushapiclient;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Upload strategy for catalog stream updates.
 * Implements the create-upload-push workflow for each batch:
 * 1. Create a new file container
 * 2. Upload content to the container
 * 3. Push the container to the stream source
 */
public class StreamUploadStrategy implements UploadStrategy<StreamUpdate> {

  private static final Logger logger = LogManager.getLogger(StreamUploadStrategy.class);

  private final StreamEnabledSource source;
  private final PlatformClient platformClient;

  public StreamUploadStrategy(StreamEnabledSource source, PlatformClient platformClient) {
    this.source = source;
    this.platformClient = platformClient;
  }

  @Override
  public HttpResponse<String> apply(StreamUpdate streamUpdate)
      throws IOException, InterruptedException {
    // Step 1: Create a new file container
    logger.info("Creating new file container");
    HttpResponse<String> createResponse = platformClient.createFileContainer();
    FileContainer container = new Gson().fromJson(createResponse.body(), FileContainer.class);

    // Step 2: Upload content to the file container
    String batchUpdateJson = new Gson().toJson(streamUpdate.marshal());
    platformClient.uploadContentToFileContainer(container, batchUpdateJson);

    // Step 3: Push the file container to the stream source
    logger.info("Pushing file container " + container.fileId + " to stream source");
    return platformClient.pushFileContainerContentToStreamSource(source.getId(), container);
  }
}
