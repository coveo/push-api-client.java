package com.coveo.pushapiclient;

/**
 * See [Manage Batches of Security Identities](https://docs.coveo.com/en/55).
 */
public record SecurityIdentityBatchConfig(String fileId, Long orderingId) {
}
