package com.coveo.pushapiclient;

/**
 * See [Disabling Old Security Identities](https://docs.coveo.com/en/33)
 */
public record SecurityIdentityDeleteOptions(Integer queueDelay, Long orderingId) {
}
