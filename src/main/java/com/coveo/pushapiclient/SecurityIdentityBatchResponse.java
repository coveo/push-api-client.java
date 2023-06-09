package com.coveo.pushapiclient;

import java.net.http.HttpResponse;

/**
 * Used for the responses when pushing batches of Security Identities. See [Manage Batches of
 * Security Identities](https://docs.coveo.com/en/55)
 */
public class SecurityIdentityBatchResponse {

  protected HttpResponse<String> s3Response;
  protected HttpResponse<String> batchResponse;

  public HttpResponse<String> getS3Response() {
    return s3Response;
  }

  public void setS3Response(HttpResponse<String> s3Response) {
    this.s3Response = s3Response;
  }

  public HttpResponse<String> getBatchResponse() {
    return batchResponse;
  }

  public void setBatchResponse(HttpResponse<String> batchResponse) {
    this.batchResponse = batchResponse;
  }
}
