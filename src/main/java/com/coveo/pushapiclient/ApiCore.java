package com.coveo.pushapiclient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ApiCore {
  private final HttpClient httpClient;
  private final Logger logger;
  private final int retryAfter;
  private final int maxRetries;

  public ApiCore() {
    this.httpClient = HttpClient.newHttpClient();
    this.logger = LogManager.getLogger(ApiCore.class);
    this.retryAfter = 5000;
    this.maxRetries = 50;
  }

  public ApiCore(HttpClient httpClient, Logger logger) {
    this.httpClient = httpClient;
    this.logger = logger;
    this.retryAfter = 5000;
    this.maxRetries = 50;
  }

  public ApiCore(HttpClient httpClient, Logger logger, int retryAfter, int maxRetries) {
    this.httpClient = httpClient;
    this.logger = logger;
    this.retryAfter = retryAfter;
    this.maxRetries = maxRetries;
  }

  public HttpResponse<String> callApiWithRetries(
      URI uri, String[] headers, int timeMultiple)
      throws Exception {
    long delayInMilliseconds = retryAfter * 1000L;
    int nbRetries = 0;

    while (true) {
      HttpResponse<String> response = this.post(uri, headers);
      nbRetries++;

      if (response.statusCode() == 429 && nbRetries <= maxRetries) {
        Thread.sleep(delayInMilliseconds);
        delayInMilliseconds = delayInMilliseconds * timeMultiple;
      } else {
        if (response.statusCode() >= 400) {
          throw new Exception("HTTP error " + response.statusCode() + " : " + response.body());
        }
        return response;
      }
    }
  }

  public HttpResponse<String> post(URI uri, String[] headers)
      throws IOException, InterruptedException {
    return this.post(uri, headers, HttpRequest.BodyPublishers.ofString(""));
  }

  public HttpResponse<String> post(URI uri, String[] headers, BodyPublisher body)
      throws IOException, InterruptedException {
    this.logger.debug("POST " + uri);
    HttpRequest request = HttpRequest.newBuilder().headers(headers).uri(uri).POST(body).build();
    HttpResponse<String> response =
        this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    this.logResponse(response);
    return response;
  }

  public HttpResponse<String> put(URI uri, String[] headers, BodyPublisher body)
      throws IOException, InterruptedException {
    this.logger.debug("PUT " + uri);
    HttpRequest request = HttpRequest.newBuilder().headers(headers).uri(uri).PUT(body).build();
    HttpResponse<String> response =
        this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    this.logResponse(response);
    return response;
  }

  public HttpResponse<String> delete(URI uri, String[] headers)
      throws IOException, InterruptedException {
    this.logger.debug("DELETE " + uri);
    HttpRequest request = HttpRequest.newBuilder().headers(headers).uri(uri).DELETE().build();
    HttpResponse<String> response =
        this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    this.logResponse(response);
    return response;
  }

  public HttpResponse<String> delete(URI uri, String[] headers, BodyPublisher body)
      throws IOException, InterruptedException {
    this.logger.debug("DELETE " + uri);
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
      this.logger.error(statusMessage);
      this.logger.error(responseMessage);
    } else {
      this.logger.debug(statusMessage);
      this.logger.debug(responseMessage);
    }
  }
}
