package com.coveo.pushapiclient;

import java.util.Arrays;
import java.util.Objects;

/**
 * Build a security identity of type `USER`.
 *
 * <p>Typically used in conjunction with {@link DocumentBuilder#withAllowedPermissions} or {@link
 * DocumentBuilder#withDeniedPermissions}.
 *
 * <p>See {@link SecurityIdentity}.
 */
public class UserSecurityIdentityBuilder implements SecurityIdentityBuilder {

  private final String[] identities;
  private final String securityProvider;

  public UserSecurityIdentityBuilder(String[] identities, String securityProvider) {
    this.identities = identities;
    this.securityProvider = securityProvider;
  }

  /**
   * Construct a UserSecurityIdentityBuilder for a single identity with the given security provider.
   *
   * @param identity
   * @param securityProvider
   */
  public UserSecurityIdentityBuilder(String identity, String securityProvider) {
    this(new String[] {identity}, securityProvider);
  }

  /**
   * Construct a UserSecurityIdentityBuilder for a single identity with an `Email Security
   * Provider`.
   *
   * @param identity
   */
  public UserSecurityIdentityBuilder(String identity) {
    this(new String[] {identity}, "Email Security Provider");
  }

  /**
   * Construct a UserSecurityIdentityBuilder for multiple identities with an `Email Security
   * Provider`.
   *
   * @param identities
   */
  public UserSecurityIdentityBuilder(String[] identities) {
    this(identities, "Email Security Provider");
  }

  public SecurityIdentity[] build() {
    return new AnySecurityIdentityBuilder(
            this.identities, SecurityIdentityType.USER, this.securityProvider)
        .build();
  }

  public String[] getIdentities() {
    return identities;
  }

  public String getSecurityProvider() {
    return securityProvider;
  }

  @Override
  public String toString() {
    return "UserSecurityIdentityBuilder["
        + "identities="
        + Arrays.toString(identities)
        + ", securityProvider='"
        + securityProvider
        + '\''
        + ']';
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    UserSecurityIdentityBuilder that = (UserSecurityIdentityBuilder) obj;
    return Arrays.equals(identities, that.identities)
        && Objects.equals(securityProvider, that.securityProvider);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(securityProvider);
    result = 31 * result + Arrays.hashCode(identities);
    return result;
  }
}
