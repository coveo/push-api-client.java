package com.coveo.pushapiclient;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ApiCore {
  private final HttpClient httpClient;
  private final Logger logger;
  private final BackoffOptions options;

  public ApiCore() {
    this(HttpClient.newHttpClient(), LogManager.getLogger(ApiCore.class));
  }

  public ApiCore(HttpClient httpClient, Logger logger) {
    this(httpClient, logger, new BackoffOptionsBuilder().build());
  }

  public ApiCore(HttpClient httpClient, Logger logger, BackoffOptions options) {
    this.httpClient = httpClient;
    this.logger = logger;
    this.options = options;
  }

  public HttpResponse<String> callApiWithRetries(HttpRequest request)
      throws IOException, InterruptedException {
    IntervalFunction intervalFn =
        IntervalFunction.ofExponentialRandomBackoff(
            this.options.getRetryAfter(), this.options.getTimeMultiple());

    RetryConfig retryConfig =
        RetryConfig.<HttpResponse<String>>custom()
            .maxAttempts(this.options.getMaxRetries())
            .intervalFunction(intervalFn)
            .retryOnResult(response -> response != null && response.statusCode() == 429)
            .build();

    Retry retry = Retry.of("platformRequest", retryConfig);

    Function<HttpRequest, HttpResponse<String>> retryRequestFn =
        Retry.decorateFunction(retry, req -> sendRequest(req));

    return retryRequestFn.apply(request);
  }

  public HttpResponse<String> sendRequest(HttpRequest request) {
    String uri = request.uri().toString();
    String reqMethod = request.method();
    this.logger.debug(reqMethod + " " + uri);
    try {
      HttpResponse<String> response =
          this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
      this.logResponse(response);
      return response;
    } catch (IOException | InterruptedException e) {
      throw new Error(e.getMessage());
    }
  }

  public HttpResponse<String> post(URI uri, String[] headers)
      throws IOException, InterruptedException {
    return this.post(uri, headers, HttpRequest.BodyPublishers.ofString(""));
  }

  public HttpResponse<String> post(URI uri, String[] headers, BodyPublisher body)
      throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().headers(headers).uri(uri).POST(body).build();
    HttpResponse<String> response = this.callApiWithRetries(request);
    return response;
  }

  public HttpResponse<String> put(URI uri, String[] headers, BodyPublisher body)
      throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().headers(headers).uri(uri).PUT(body).build();
    HttpResponse<String> response = this.callApiWithRetries(request);
    return response;
  }

  public HttpResponse<String> delete(URI uri, String[] headers)
      throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().headers(headers).uri(uri).DELETE().build();
    HttpResponse<String> response = this.callApiWithRetries(request);
    return response;
  }

  public HttpResponse<String> delete(URI uri, String[] headers, BodyPublisher body)
      throws IOException, InterruptedException {
    HttpRequest request =
        HttpRequest.newBuilder().headers(headers).uri(uri).method("DELETE", body).build();
    HttpResponse<String> response = this.callApiWithRetries(request);
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
