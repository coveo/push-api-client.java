package com.coveo.pushapiclient;

import java.io.IOException;

public class PushServiceInternal {
  private DocumentUploadQueue queue;

  public PushServiceInternal(DocumentUploadQueue queue) {
    this.queue = queue;
  }

  public void addOrUpdate(DocumentBuilder document) throws IOException, InterruptedException {
    this.queue.add(document);
  }

  public void delete(DeleteDocument document) throws IOException, InterruptedException {
    this.queue.add(document);
  }

  public void close() throws IOException, InterruptedException {
    queue.flush();
  }
}
