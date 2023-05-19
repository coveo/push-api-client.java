package com.coveo.pushapiclient;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

public class ApiUrlTest {

    private ApiUrl defaultUrl;
    private ApiUrl regionOnlyUrl;
    private ApiUrl regionOnlyUrlCaseInsensitive;
    private ApiUrl environmentOnlyUrl;
    private ApiUrl environmentAndRegionUrl;
    private ApiUrl streamURL;

    @Before
    public void setUp() throws MalformedURLException {
        defaultUrl = new ApiUrl(
                new URL("https://api.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
        regionOnlyUrl = new ApiUrl(
                new URL("https://api-au.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
        regionOnlyUrlCaseInsensitive = new ApiUrl(
                new URL("https://api-EU.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
        environmentOnlyUrl = new ApiUrl(
                new URL("https://apidev.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
        environmentAndRegionUrl = new ApiUrl(
                new URL("https://apidev-au.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));
        streamURL = new ApiUrl(
                new URL("https://apidev-au.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/stream/open"));

    }

    @Test
    public void testSourceId() {
        assertEquals(defaultUrl.getSourceId(), "my-source-id");
        assertEquals(regionOnlyUrl.getSourceId(), "my-source-id");
        assertEquals(regionOnlyUrlCaseInsensitive.getSourceId(), "my-source-id");
        assertEquals(environmentOnlyUrl.getSourceId(), "my-source-id");
        assertEquals(environmentAndRegionUrl.getSourceId(), "my-source-id");
        assertEquals(streamURL.getSourceId(), "my-source-id");
    }

    @Test
    public void testOrganizationId() {
        assertEquals(defaultUrl.getOrganizationId(), "my-org-id");
        assertEquals(regionOnlyUrl.getOrganizationId(), "my-org-id");
        assertEquals(regionOnlyUrlCaseInsensitive.getOrganizationId(), "my-org-id");
        assertEquals(environmentOnlyUrl.getOrganizationId(), "my-org-id");
        assertEquals(environmentAndRegionUrl.getOrganizationId(), "my-org-id");
        assertEquals(streamURL.getOrganizationId(), "my-org-id");
    }

    @Test
    public void testPlatformUrl() {
        assertEquals(defaultUrl.getPlatformUrl().getApiUrl(), "https://api.cloud.coveo.com");
        assertEquals(regionOnlyUrl.getPlatformUrl().getApiUrl(), "https://api-au.cloud.coveo.com");
        assertEquals(regionOnlyUrlCaseInsensitive.getPlatformUrl().getApiUrl(), "https://api-eu.cloud.coveo.com");
        assertEquals(environmentOnlyUrl.getPlatformUrl().getApiUrl(), "https://apidev.cloud.coveo.com");
        assertEquals(environmentAndRegionUrl.getPlatformUrl().getApiUrl(), "https://apidev-au.cloud.coveo.com");
        assertEquals(streamURL.getPlatformUrl().getApiUrl(), "https://apidev-au.cloud.coveo.com");
    }

    @Test(expected = MalformedURLException.class)
    public void testInvalidEnvironementUrl() throws MalformedURLException {
        defaultUrl = new ApiUrl(
                new URL("https://apifoo.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));

    }

    @Test(expected = MalformedURLException.class)
    public void testInvalidRegionUrl() throws MalformedURLException {
        defaultUrl = new ApiUrl(
                new URL("https://api-bar.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));

    }

    @Test(expected = MalformedURLException.class)
    public void testInvalidPathUrl() throws MalformedURLException {
        defaultUrl = new ApiUrl(
                new URL("https://api.cloud.coveo.com/push/v1/organizations/my-org-id/providers/provider-id/mappings"));

    }

    @Test(expected = MalformedURLException.class)
    public void testInvalidHostUrl() throws MalformedURLException {
        defaultUrl = new ApiUrl(
                new URL("https://platform.cloud.coveo.com/push/v1/organizations/my-org-id/sources/my-source-id/documents"));

    }
}
