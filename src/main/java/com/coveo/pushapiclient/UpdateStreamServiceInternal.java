package com.coveo.pushapiclient;

import com.coveo.pushapiclient.exceptions.NoOpenFileContainerException;
import java.io.IOException;
import java.net.http.HttpResponse;
import org.apache.logging.log4j.Logger;

/** For internal use only. Made to easily test the service without having to use PowerMock */
class UpdateStreamServiceInternal {
  private final StreamDocumentUploadQueue queue;


  public UpdateStreamServiceInternal(
      final StreamEnabledSource source,
      final StreamDocumentUploadQueue queue,
      final PlatformClient platformClient,
      final Logger logger) {
    this.queue = queue;
  }

  public void addOrUpdate(DocumentBuilder document)
      throws IOException, InterruptedException {
    queue.add(document);
  }

  public void addPartialUpdate(PartialUpdateDocument document)
      throws IOException, InterruptedException {
    queue.add(document);
  }

  public void delete(DeleteDocument document) throws IOException, InterruptedException {
    queue.add(document);
  }

  public HttpResponse<String> close()
      throws IOException, InterruptedException, NoOpenFileContainerException {
    queue.flush();
    return queue.getLastResponse();
  }
}
