package com.coveo.pushapiclient;

/**
 * Available environments to use as the host for the PushAPI.
 */
public enum Environment {
    PRODUCTION( "https://api.cloud.coveo.com"),
    HIPAA("https://apihipaa.cloud.coveo.com"),
    DEVELOPMENT("https://apidev.cloud.coveo.com"),
    STAGING("https://apiqa.cloud.coveo.com");

    private String host;

    Environment(String host) {
        this.host = host;
    }

    public String getHost() {
        return this.host;
    }
}
