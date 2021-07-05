package com.coveo.pushapiclient;

public class SecurityIdentityBatchConfig {
    public final String fileId;
    public final Long orderingId;

    public SecurityIdentityBatchConfig(String fileId, Long orderingId) {
        this.fileId = fileId;
        this.orderingId = orderingId;
    }
}
