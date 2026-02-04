package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.http.HttpResponse;

/**
 * Functional interface for stream upload operations with a three-step workflow contract.
 *
 * <p>Implementations of this interface handle the complete stream upload workflow:
 * <ol>
 *   <li>Create a file container via {@code platformClient.createFileContainer()}
 *   <li>Upload content to the container via {@code platformClient.uploadContentToFileContainer()}
 *   <li>Push the container content to the stream source via {@code
 *       platformClient.pushFileContainerContentToStreamSource()}
 * </ol>
 *
 * <p>This is an internal implementation detail and should only be used within the package for
 * handling stream-specific upload operations.
 */
@FunctionalInterface
interface StreamUploadHandler {
  /**
   * Handles a stream update by executing the upload and push workflow.
   *
   * @param stream the {@link StreamUpdate} containing documents and operations to push
   * @return the HTTP response from the push operation
   * @throws IOException if an I/O error occurs during upload or push operations
   * @throws InterruptedException if the operation is interrupted
   */
  HttpResponse<String> uploadAndPush(StreamUpdate stream) throws IOException, InterruptedException;
}
