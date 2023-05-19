package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.http.HttpResponse;

@FunctionalInterface
public interface UpdloadStrategy {
    HttpResponse<String> apply(BatchUpdate batchUpdate) throws IOException, InterruptedException;
}
