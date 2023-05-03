package com.coveo.pushclient;

import com.coveo.platform.Environment;
import com.coveo.platform.PlatformUrl;
import com.coveo.platform.PlatformUrlBuilder;
import com.coveo.platform.Region;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class PlatformUrlBuilderTest {

    private PlatformUrlBuilder platformUrlBuilder;

    @Before
    public void setup() {
        platformUrlBuilder = new PlatformUrlBuilder();
    }

    @Test
    public void testWithDefaultValues() {
        PlatformUrl platformUrl = platformUrlBuilder.build();
        assertEquals(
                "Should return default platform URL",
                "https://platform.cloud.coveo.com",
                platformUrl.getPlatformUrl());

        assertEquals(
                "Should return default API URL",
                "https://api.cloud.coveo.com",
                platformUrl.getApiUrl());
    }

    @Test
    public void testWithNonDefaultRegion() {
        PlatformUrl platformUrl = platformUrlBuilder.withRegion(Region.EU).build();
        assertEquals(
                "Should return Europe platform URL",
                "https://platform-eu.cloud.coveo.com",
                platformUrl.getPlatformUrl());

        assertEquals(
                "Should return Europe API URL",
                "https://api-eu.cloud.coveo.com",
                platformUrl.getApiUrl());
    }

    @Test
    public void testWithNonDefaultEnvironment() {
        PlatformUrl platformUrl = platformUrlBuilder.withEnvironment(Environment.STAGING).build();
        assertEquals(
                "Should return the staging platform URL",
                "https://platformstg.cloud.coveo.com",
                platformUrl.getPlatformUrl());

        assertEquals(
                "Should return the staging API URL",
                "https://apistg.cloud.coveo.com",
                platformUrl.getApiUrl());
    }

    @Test
    public void testWithNonDefaultEnvironmentAndRegion() {
        PlatformUrl platformUrl = platformUrlBuilder
                .withEnvironment(Environment.DEVELOPMENT)
                .withRegion(Region.EU)
                .build();
        assertEquals(
                "https://platformdev-eu.cloud.coveo.com",
                platformUrl.getPlatformUrl());

        assertEquals(
                "https://apidev-eu.cloud.coveo.com",
                platformUrl.getApiUrl());
    }
}
