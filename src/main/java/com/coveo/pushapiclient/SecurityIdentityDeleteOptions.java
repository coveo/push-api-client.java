package com.coveo.pushapiclient;

public class SecurityIdentityDeleteOptions {
    public final Integer queueDelay;
    public final Long orderingId;

    public SecurityIdentityDeleteOptions(Integer queueDelay, Long orderingId) {
        this.queueDelay = queueDelay;
        this.orderingId = orderingId;
    }
}
