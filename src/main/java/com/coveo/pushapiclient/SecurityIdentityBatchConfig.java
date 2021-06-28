package com.coveo.pushapiclient;

public class SecurityIdentityBatchConfig {
    public String fileId;
    public Long orderingId;

    public SecurityIdentityBatchConfig(String fileId, Long orderingId) {
        this.fileId = fileId;
        this.orderingId = orderingId;
    }
}
