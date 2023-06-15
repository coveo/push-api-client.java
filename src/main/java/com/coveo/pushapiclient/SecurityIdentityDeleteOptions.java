package com.coveo.pushapiclient;

import java.util.Objects;

/**
 * @see <a href="https://docs.coveo.com/en/33">Disabling Old Security Identities</a>
 */
public class SecurityIdentityDeleteOptions {

  private final Integer queueDelay;
  private final Long orderingId;

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

  @Override
  public String toString() {
    return "SecurityIdentityDeleteOptions"
        + "["
        + "queueDelay="
        + queueDelay
        + ", orderingId="
        + orderingId
        + ']';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    SecurityIdentityDeleteOptions that = (SecurityIdentityDeleteOptions) obj;
    return Objects.equals(queueDelay, that.queueDelay)
        && Objects.equals(orderingId, that.orderingId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(queueDelay, orderingId);
  }
}
