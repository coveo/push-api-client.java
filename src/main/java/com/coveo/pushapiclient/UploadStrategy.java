package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.http.HttpResponse;

@FunctionalInterface
public interface UploadStrategy<T extends UploadContent> {
  HttpResponse<String> apply(T content) throws IOException, InterruptedException;
}
