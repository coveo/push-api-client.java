package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;

// TODO: LENS-934 - Support throttling
class ApiCore {
  private final HttpClient httpClient;

  public ApiCore() {
    this.httpClient = HttpClient.newHttpClient();
  }

  public ApiCore(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  public HttpResponse<String> post(URI uri, String[] headers)
      throws IOException, InterruptedException {
    return this.post(uri, headers, HttpRequest.BodyPublishers.ofString(""));
  }

  public HttpResponse<String> post(URI uri, String[] headers, BodyPublisher body)
      throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().headers(headers).uri(uri).POST(body).build();
    return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  public HttpResponse<String> put(URI uri, String[] headers, BodyPublisher body)
      throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().headers(headers).uri(uri).PUT(body).build();
    return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  public HttpResponse<String> delete(URI uri, String[] headers)
      throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().headers(headers).uri(uri).DELETE().build();
    return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  public HttpResponse<String> delete(URI uri, String[] headers, BodyPublisher body)
      throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder().headers(headers).uri(uri).method("DELETE", body).build();
    return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }
}
