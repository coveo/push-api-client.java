package com.coveo.pushapiclient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ApiCoreTest {

  @Mock private HttpClient httpClient;
  @Mock private HttpRequest httpRequest;
  @Mock private Logger logger;
  @Mock private BackoffOptions backoffOptions;
  @Mock private HttpResponse<String> httpResponse;

  @InjectMocks private ApiCore api;

  private AutoCloseable closeable;
  private static final String[] headers = {
    "Content-Type", "application/json", "Accept", "application/json"
  };

  private void mockSuccessResponse() {
    when(httpResponse.statusCode()).thenReturn(200);
    when(httpResponse.body()).thenReturn("All good!");
    when(httpRequest.method()).thenReturn("POST");
  }

  private void mockErrorResponse() {
    when(httpResponse.statusCode()).thenReturn(412);
    when(httpResponse.body()).thenReturn("BAD_REQUEST");
    when(httpRequest.method()).thenReturn("DELETE");
  }

  private void mockThrottledResponse() {
    when(httpResponse.statusCode()).thenReturn(429);
    when(httpResponse.body()).thenReturn("THROTTLED_REQUEST");
    when(httpRequest.method()).thenReturn("POST");
  }

  private void mockBackoffOptions() {
    when(backoffOptions.getMaxRetries()).thenReturn(2);
    when(backoffOptions.getRetryAfter()).thenReturn(100);
    when(backoffOptions.getTimeMultiple()).thenReturn(2);
  }

  @Before
  public void setUp() throws Exception {
    closeable = MockitoAnnotations.openMocks(this);

    when(httpClient.send(any(HttpRequest.class), any(BodyHandler.class))).thenReturn(httpResponse);
    when(httpResponse.request()).thenReturn(httpRequest);
    mockBackoffOptions();
  }

  @After
  public void closeService() throws Exception {
    closeable.close();
  }

  @Test
  public void testShouldLogRequestAndResonse()
      throws IOException, InterruptedException, URISyntaxException {
    this.mockSuccessResponse();
    this.api.post(new URI("https://perdu.com/"), headers);

    verify(logger, times(1)).debug("POST https://perdu.com/");
    verify(logger, times(1)).debug("POST status: 200");
    verify(logger, times(1)).debug("POST response: All good!");
  }

  @Test
  public void testShouldLogResponse() throws IOException, InterruptedException, URISyntaxException {
    this.mockErrorResponse();
    this.api.delete(new URI("https://perdu.com/"), headers);

    verify(logger, times(1)).debug("DELETE https://perdu.com/");
    verify(logger, times(1)).error("DELETE status: 412");
    verify(logger, times(1)).error("DELETE response: BAD_REQUEST");
  }

  @Test
  public void testShouldHandleBackoffOptions()
      throws IOException, InterruptedException, URISyntaxException {
    this.mockThrottledResponse();

    this.api.post(new URI("https://perdu.com/"), headers);

    verify(logger, times(2)).debug("POST https://perdu.com/");
    verify(logger, times(2)).error("POST status: 429");
    verify(logger, times(2)).error("POST response: THROTTLED_REQUEST");
  }
}
