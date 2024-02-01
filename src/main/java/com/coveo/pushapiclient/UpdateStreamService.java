package com.coveo.pushapiclient;

import com.coveo.pushapiclient.exceptions.NoOpenFileContainerException;
import com.coveo.pushapiclient.exceptions.NoOpenStreamException;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateStreamService {

  private final PlatformClient platformClient;
  private final StreamServiceInternal streamServiceInternal;

  private FileContainer fileContainer;

  /**
   * Creates a service to stream your documents to the provided source by interacting with the
   * Stream API. This provides the ability to incrementally add, update, or delete documents via a
   * stream.
   *
   * <p>To perform <a href="https://docs.coveo.com/en/lb4a0344">a full source rebuild</a>, use the
   * {@StreamService}
   *
   * @param source The source to which you want to send your documents.
   */
  public UpdateStreamService(StreamEnabledSource source) {
    this(source, new BackoffOptionsBuilder().build());
  }

  /**
   * Creates a service to stream your documents to the provided source by interacting with the
   * Stream API. This provides the ability to incrementally add, update, or delete documents via a
   * stream.
   *
   * <p>To perform <a href="https://docs.coveo.com/en/lb4a0344">a full source rebuild</a>, use the
   * {@StreamService}
   *
   * @param source The source to which you want to send your documents.
   * @param options The configuration options for exponential backoff.
   */
  public UpdateStreamService(StreamEnabledSource source, BackoffOptions options) {
    Logger logger = LogManager.getLogger(UpdateStreamService.class);
    this.platformClient =
        new PlatformClient(
            source.getApiKey(), source.getOrganizationId(), source.getPlatformUrl(), options);
    this.streamServiceInternal =
        new StreamServiceInternal(
            source,
            new DocumentUploadQueue(this.getUploadStrategy()),
            this.platformClient,
            StreamOperationType.INCREMENTAL,
            logger);
  }

  /**
   * Adds documents to an open file container be created or updated. If there is no file container
   * open to receive the documents, this function will open a file container before uploading
   * documents into it.
   *
   * <p>If called several times, the service will automatically batch documents and create new
   * stream chunks whenever the data payload exceeds the <a
   * href="https://docs.coveo.com/en/lb4a0344#stream-api-limits">batch size limit</a> set for the
   * Stream API.
   *
   * <p>Once there are no more documents to add, it is important to call the {@link
   * UpdateStreamService#close} function in order to send any buffered documents and push the file
   * container. Otherwise, changes will not be reflected in the index.
   *
   * <p>
   *
   * <pre>{@code
   * //...
   * UpdateStreamService service = new UpdateStreamService(source));
   * for (DocumentBuilder document : fictionalDocumentList) {
   *     service.addOrUpdate(document);
   * }
   * service.close(document);
   * }</pre>
   *
   * <p>For more code samples, @see `samples/UpdateStreamDocuments.java`
   *
   * @param document The documentBuilder to push to your file container
   * @throws InterruptedException If the creation of the file container or adding the document is
   *     interrupted.
   * @throws IOException If the creation of the file container or adding the document fails.
   */
  public void addOrUpdate(DocumentBuilder document) throws IOException, InterruptedException {
    fileContainer = streamServiceInternal.addOrUpdate(document);
  }

  /**
   * Adds documents to an open file container be deleted. If there is no file container open to
   * receive the documents, this function will open a file container before uploading documents into
   * it.
   *
   * <p>If called several times, the service will automatically batch documents and create new
   * stream chunks whenever the data payload exceeds the <a
   * href="https://docs.coveo.com/en/lb4a0344#stream-api-limits">batch size limit</a> set for the
   * Stream API.
   *
   * <p>Once there are no more documents to add, it is important to call the {@link
   * UpdateStreamService#close} function in order to send any buffered documents and push the file
   * container. Otherwise, changes will not be reflected in the index.
   *
   * <p>
   *
   * <pre>{@code
   * //...
   * UpdateStreamService service = new UpdateStreamService(source));
   * for (DeleteDocument document : fictionalDocumentList) {
   *     service.delete(document);
   * }
   * service.close(document);
   * }</pre>
   *
   * <p>For more code samples, @see `samples/UpdateStreamDocuments.java`
   *
   * @param document The deleteDocument to push to your file container
   * @throws InterruptedException If the creation of the file container or adding the document is
   *     interrupted.
   * @throws IOException If the creation of the file container or adding the document fails.
   */
  public void delete(DeleteDocument document) throws IOException, InterruptedException {
    fileContainer = streamServiceInternal.delete(document);
  }

  /**
   * Sends any buffered documents and <a
   * href="https://docs.coveo.com/en/l62e0540/how-to-update-your-catalog#step-3-send-the-file-container-to-update-your-catalog">pushes
   * the file container</a>.
   *
   * <p>Upon invoking this method, any documents added to the file container will be pushed and
   * indexed.
   *
   * @return The HttpResponse from the platform.
   * @throws IOException If the pushing file container failed.
   * @throws InterruptedException If the pushing file container is interrupted.
   * @throws NoOpenFileContainerException If there is no open file container to push.
   */
  public HttpResponse<String> close()
      throws IOException, InterruptedException, NoOpenFileContainerException {
    try {
      return streamServiceInternal.close();
    } catch (NoOpenStreamException e) {
      throw new IllegalStateException(
          "The stream service should only ever open up a stream, so we should never encounter this scenario",
          e);
    }
  }

  private UploadStrategy getUploadStrategy() {
    return (batchUpdate) -> {
      String batchUpdateJson = new Gson().toJson(batchUpdate.marshal());
      return this.platformClient.uploadContentToFileContainer(fileContainer, batchUpdateJson);
    };
  }
}
