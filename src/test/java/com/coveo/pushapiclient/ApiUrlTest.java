package com.coveo.pushapiclient;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;

public class ApiUrlTest {

  @Test
  public void testSourceId() throws MalformedURLException {
    ApiUrl url =
        new ApiUrl(
            new URL(
                "https://api.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
    assertEquals(url.getSourceId(), "my-source-id");
  }

  @Test
  public void testOrganizationId() throws MalformedURLException {
    ApiUrl url =
        new ApiUrl(
            new URL(
                "https://api.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
    assertEquals(url.getOrganizationId(), "my-org-id");
  }

  @Test
  public void testPlatformUrl() throws MalformedURLException {
    ApiUrl defaultUrl =
        new ApiUrl(
            new URL(
                "https://api.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
    ApiUrl regionOnlyUrl =
        new ApiUrl(
            new URL(
                "https://api-au.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
    ApiUrl environmentOnlyUrl =
        new ApiUrl(
            new URL(
                "https://apidev.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
    ApiUrl environmentAndRegionUrl =
        new ApiUrl(
            new URL(
                "https://apidev-au.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));

    assertEquals(defaultUrl.getPlatformUrl().getApiUrl(), "https://api.cloud.coveo.com");
    assertEquals(regionOnlyUrl.getPlatformUrl().getApiUrl(), "https://api-au.cloud.coveo.com");
    assertEquals(environmentOnlyUrl.getPlatformUrl().getApiUrl(), "https://apidev.cloud.coveo.com");
    assertEquals(
        environmentAndRegionUrl.getPlatformUrl().getApiUrl(), "https://apidev-au.cloud.coveo.com");
  }

  @Test
  public void testStreamApiUrl() throws MalformedURLException {
    ApiUrl url =
        new ApiUrl(
            new URL(
                "https://apidev-au.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/stream/open"));

    assertEquals(url.getPlatformUrl().getApiUrl(), "https://apidev-au.cloud.coveo.com");
    assertEquals(url.getOrganizationId(), "my-org-id");
    assertEquals(url.getSourceId(), "my-source-id");
  }

  @Test(expected = MalformedURLException.class)
  public void testInvalidEnvironmentUrl() throws MalformedURLException {
    new ApiUrl(
        new URL(
            "https://apifoo.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
  }

  @Test(expected = MalformedURLException.class)
  public void testInvalidUrl() throws MalformedURLException {
    new ApiUrl(
        new URL(
            "https://apifoo.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
  }

  @Test(expected = MalformedURLException.class)
  public void testInvalidRegionUrl() throws MalformedURLException {
    new ApiUrl(
        new URL(
            "https://api-bar.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
  }

  @Test(expected = MalformedURLException.class)
  public void testInvalidPathUrl() throws MalformedURLException {
    new ApiUrl(
        new URL(
            "https://api.cloud.coveo.com/push/v1/organizations/my-org-id/providers/provider-id/mappings"));
  }

  @Test(expected = MalformedURLException.class)
  public void testInvalidHostUrl() throws MalformedURLException {
    new ApiUrl(
        new URL(
            "https://platform.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
  }
}
