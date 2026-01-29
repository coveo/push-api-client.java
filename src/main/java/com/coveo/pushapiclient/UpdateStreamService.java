package com.coveo.pushapiclient;

import com.coveo.pushapiclient.exceptions.NoOpenFileContainerException;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UpdateStreamService {

  private final PlatformClient platformClient;
  private final UpdateStreamServiceInternal updateStreamServiceInternal;

  /**
   * Creates a service to stream your documents to the provided source by interacting with the
   * Stream API. This provides the ability to incrementally add, update, or delete documents via a
   * stream.
   *
   * <p>To perform <a href="https://docs.coveo.com/en/lb4a0344">a full source rebuild</a>, use the
   * {@StreamService}
   *
   * @param source The source to which you want to send your documents.
   * @param userAgents The user agent to use for the requests.
   */
  public UpdateStreamService(StreamEnabledSource source, String[] userAgents) {
    this(source, new BackoffOptionsBuilder().build(), userAgents, DocumentUploadQueue.getConfiguredBatchSize());
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
   */
  public UpdateStreamService(StreamEnabledSource source) {
    this(source, new BackoffOptionsBuilder().build(), null, DocumentUploadQueue.getConfiguredBatchSize());
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
    this(source, options, null, DocumentUploadQueue.getConfiguredBatchSize());
  }

  /**
   * Creates a service to stream your documents to the provided source by interacting with the
   * Stream API. This provides the ability to incrementally add, update, or delete documents via a
   * stream.
   *
   * <p>To perform <a href="https://docs.coveo.com/en/lb4a0344">a full source rebuild</a>, use the
   * {@link StreamService}.
   *
   * @param source The source to push to
   * @param options The backoff parameters
   * @param userAgents The user-agents to append to the "User-Agent" HTTP header when performing
   *     requests against the Coveo Platform.
   */
  public UpdateStreamService(
      StreamEnabledSource source, BackoffOptions options, String[] userAgents) {
    this(source, options, userAgents, DocumentUploadQueue.getConfiguredBatchSize());
  }

  /**
   * Creates a service to stream your documents to the provided source by interacting with the
   * Stream API. This provides the ability to incrementally add, update, or delete documents via a
   * stream.
   *
   * <p>To perform <a href="https://docs.coveo.com/en/lb4a0344">a full source rebuild</a>, use the
   * {@StreamService}
   *
   * <p>Example batch sizes in bytes:
   * <ul>
   *   <li>5 MB (default): {@code 5 * 1024 * 1024} = {@code 5242880}
   *   <li>50 MB: {@code 50 * 1024 * 1024} = {@code 52428800}
   *   <li>256 MB (max): {@code 256 * 1024 * 1024} = {@code 268435456}
   * </ul>
   *
   * @param source The source to which you want to send your documents.
   * @param options The configuration options for exponential backoff.
   * @param userAgents The user agent to use for the requests.
   * @param maxQueueSize The maximum batch size in bytes before auto-flushing (default: 5MB, max:
   *     256MB).
   * @throws IllegalArgumentException if maxQueueSize exceeds 256MB or is not positive.
   */
  public UpdateStreamService(
      StreamEnabledSource source, BackoffOptions options, String[] userAgents, int maxQueueSize) {
    Logger logger = LogManager.getLogger(UpdateStreamService.class);
    this.platformClient =
        new PlatformClient(
            source.getApiKey(), source.getOrganizationId(), source.getPlatformUrl(), options);
    if (userAgents != null) {
      this.platformClient.setUserAgents(userAgents);
    }
    this.updateStreamServiceInternal =
        new UpdateStreamServiceInternal(
            source,
            new StreamDocumentUploadQueue(null, maxQueueSize),  // UploadStrategy no longer needed
            this.platformClient,
            logger);
  }

  /**
   * Adds documents to an open file container be created or updated. If there is no file container
   * open to receive the documents, this function will open a file container before uploading
   * documents into it.
   *
   * <p>If called several times, the service will automatically batch documents and create new
   * file containers whenever the data payload exceeds the batch size limit (default: 5MB, configurable via constructor).
   * Each batch is sent to its own file container and immediately pushed to the stream
   * source, following the <a href="https://docs.coveo.com/en/p4eb0129/coveo-for-commerce/full-catalog-data-updates#update-operations">
   * catalog stream API best practices</a>.
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
    updateStreamServiceInternal.addOrUpdate(document);
  }

  /**
   * Adds a document containing the specific field, and it's value to be updated. If there is no
   * file container open to receive the documents, this function will open a file container before
   * uploading the partial update details into it. More details on partial updates can be found in
   * the <a
   * href="https://docs.coveo.com/en/l62e0540/coveo-for-commerce/how-to-update-your-catalog#partial-item-updates">
   * Partial item updates</a> section.
   *
   * <p>If called several times, the service will automatically batch documents and create new
   * file containers whenever the data payload exceeds the batch size limit (default: 5MB, configurable via constructor).
   * Each batch is sent to its own file container and immediately pushed to the stream
   * source, following the <a href="https://docs.coveo.com/en/p4eb0129/coveo-for-commerce/full-catalog-data-updates#update-operations">
   * catalog stream API best practices</a>.
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
   * for (PartialUpdateDocument document : fictionalDocumentList) {
   *     service.addPartialUpdate(document);
   * }
   * service.close(document);
   * }</pre>
   *
   * <p>For more code samples, @see `samples/UpdateStreamDocuments.java`
   *
   * @param document The partial update document to push to your file container
   * @throws InterruptedException If the creation of the file container or adding the document is
   *     interrupted.
   * @throws IOException If the creation of the file container or adding the document fails.
   */
  public void addPartialUpdate(PartialUpdateDocument document)
      throws IOException, InterruptedException {
    updateStreamServiceInternal.addPartialUpdate(document);
  }

  /**
   * Adds documents to an open file container be deleted. If there is no file container open to
   * receive the documents, this function will open a file container before uploading documents into
   * it.
   *
   * <p>If called several times, the service will automatically batch documents and create new
   * file containers whenever the data payload exceeds the batch size limit (default: 5MB, configurable via constructor).
   * Each batch is sent to its own file container and immediately pushed to the stream
   * source, following the <a href="https://docs.coveo.com/en/p4eb0129/coveo-for-commerce/full-catalog-data-updates#update-operations">
   * catalog stream API best practices</a>.
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
    updateStreamServiceInternal.delete(document);
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
    return updateStreamServiceInternal.close();
  }
}
