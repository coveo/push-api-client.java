package com.coveo.pushapiclient;

/**
 * See [Manage Batches of Security Identities](https://docs.coveo.com/en/55).
 */
public class SecurityIdentityBatchConfig {

    private String fileId;
    private Long orderingId;

    public SecurityIdentityBatchConfig(String fileId, Long orderingId) {
        this.fileId = fileId;
        this.orderingId = orderingId;
    }

    public String getFileId() {
        return fileId;
    }

    public Long getOrderingId() {
        return orderingId;
    }
}
