package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO: LENS-934 - Support throttling
class ApiCore {
  private final HttpClient httpClient;
  private static final Logger logger = LogManager.getLogger(ApiCore.class);

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
    logger.debug("POST " + uri);
    HttpRequest request = HttpRequest.newBuilder().headers(headers).uri(uri).POST(body).build();
    HttpResponse<String> response =
        this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    this.logResponse(response);
    return response;
  }

  public HttpResponse<String> put(URI uri, String[] headers, BodyPublisher body)
      throws IOException, InterruptedException {
    logger.debug("PUT " + uri);
    HttpRequest request = HttpRequest.newBuilder().headers(headers).uri(uri).PUT(body).build();
    HttpResponse<String> response =
        this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    this.logResponse(response);
    return response;
  }

  public HttpResponse<String> delete(URI uri, String[] headers)
      throws IOException, InterruptedException {
    logger.debug("DELETE " + uri);
    HttpRequest request = HttpRequest.newBuilder().headers(headers).uri(uri).DELETE().build();
    HttpResponse<String> response =
        this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    this.logResponse(response);
    return response;
  }

  public HttpResponse<String> delete(URI uri, String[] headers, BodyPublisher body)
      throws IOException, InterruptedException {
    logger.debug("DELETE " + uri);
    HttpRequest request =
        HttpRequest.newBuilder().headers(headers).uri(uri).method("DELETE", body).build();
    HttpResponse<String> response =
        this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    this.logResponse(response);
    return response;
  }

  private void logResponse(HttpResponse<String> response) {
    if (response == null) {
      return;
    }
    int status = response.statusCode();
    String method = response.request().method();
    String statusMessage = method + " status: " + status;
    String responseMessage = method + " response: " + response.body();

    if (status < 200 || status >= 300) {
      logger.error(statusMessage);
      logger.error(responseMessage);
    } else {
      logger.debug(statusMessage);
      logger.debug(responseMessage);
    }
  }
}
