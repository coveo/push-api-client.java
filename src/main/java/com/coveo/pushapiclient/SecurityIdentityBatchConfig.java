package com.coveo.pushapiclient;

import java.util.Objects;

/**
 * @see <a href="https://docs.coveo.com/en/55">Manage Batches of Security Identities</a>
 */
public class SecurityIdentityBatchConfig {

  private final String fileId;
  private final Long orderingId;

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

  @Override
  public String toString() {
    return "SecurityIdentityBatchConfig["
        + "fileId='"
        + fileId
        + '\''
        + ", orderingId="
        + orderingId
        + ']';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    SecurityIdentityBatchConfig that = (SecurityIdentityBatchConfig) obj;
    return Objects.equals(fileId, that.fileId) && Objects.equals(orderingId, that.orderingId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileId, orderingId);
  }
}
