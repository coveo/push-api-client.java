package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.http.HttpResponse;

@FunctionalInterface
public interface UploadStrategy {
  HttpResponse<String> apply(BatchUpdate batchUpdate) throws IOException, InterruptedException;
}
