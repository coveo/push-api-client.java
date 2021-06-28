package com.coveo.pushapiclient;

public class SecurityIdentityDeleteOptions {
    public Integer queueDelay;
    public Long orderingId;

    public SecurityIdentityDeleteOptions(Integer queueDelay, Long orderingId) {
        this.queueDelay = queueDelay;
        this.orderingId = orderingId;
    }
}
