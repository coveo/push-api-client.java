package com.coveo.pushapiclient;

/**
 * Enum for possible PushAPI statuses. See [Updating the Status of a Push Source](https://docs.coveo.com/en/35).
 */
public enum PushAPIStatus {
    IDLE,
    REBUILD,
    INCREMENTAL,
    REFRESH;
}
