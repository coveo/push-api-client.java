package com.coveo.pushapiclient;

/** Build a security identity. See {@link SecurityIdentity}. */
public interface SecurityIdentityBuilder {
  /**
   * Build and return a list of {@link SecurityIdentity}
   *
   * @return
   */
  SecurityIdentity[] build();
}
