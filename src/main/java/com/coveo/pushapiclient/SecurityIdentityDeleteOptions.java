package com.coveo.pushapiclient;

/**
 * See [Disabling Old Security Identities](https://docs.coveo.com/en/33)
 */
public class SecurityIdentityDeleteOptions {

    private Integer queueDelay;
    private Long orderingId;

    public SecurityIdentityDeleteOptions(Integer queueDelay, Long orderingId) {
        this.queueDelay = queueDelay;
        this.orderingId = orderingId;
    }

    public Integer getQueueDelay() {
        return queueDelay;
    }

    public Long getOrderingId() {
        return orderingId;
    }
}
