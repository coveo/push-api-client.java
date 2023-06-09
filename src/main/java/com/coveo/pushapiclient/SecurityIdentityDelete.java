package com.coveo.pushapiclient;

import java.util.Objects;

/** See [Disabling a Single Security Identity](https://docs.coveo.com/en/84) */
public class SecurityIdentityDelete {

  private final IdentityModel identity;

  public SecurityIdentityDelete(IdentityModel identity) {
    this.identity = identity;
  }

  public IdentityModel getIdentity() {
    return identity;
  }

  @Override
  public String toString() {
    return "SecurityIdentityDelete[" + "identity=" + identity + ']';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    SecurityIdentityDelete that = (SecurityIdentityDelete) obj;
    return Objects.equals(identity, that.identity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(identity);
  }
}
