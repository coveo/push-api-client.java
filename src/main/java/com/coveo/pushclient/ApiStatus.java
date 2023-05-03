package com.coveo.pushclient;

/**
 * Enum for possible PushAPI statuses. See [Updating the Status of a Push SourceClient](https://docs.coveo.com/en/35).
 */
public enum ApiStatus {
    IDLE,
    REBUILD,
    INCREMENTAL,
    REFRESH;
}
