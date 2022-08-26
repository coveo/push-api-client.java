package com.coveo.pushapiclient;

public class PlatformUrl {

    private final Environment environment;
    private final Region region;

    /**
     * @param environment The environment platform of your organization
     * @param region      The physical center of your organization
     *
     * @see https://docs.coveo.com/en/2976
     */
    public PlatformUrl(Environment environment, Region region) {
        this.environment = environment;
        this.region = region;
    }

    public String getPlatformUrl() {
        return String.format("https://platform%s%s.cloud.coveo.com", this.environment, this.region);
    }

    public String getApiUrl() {
        return String.format("https://api%s%s.cloud.coveo.com", this.environment, this.region);
    }
}
