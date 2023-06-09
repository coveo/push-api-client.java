package com.coveo.pushapiclient;

public class SecurityIdentity {
  /**
   * The name of the security identity.
   *
   * <p>Examples: - `asmith@example.com` - `SampleTeam2`
   */
  public String identity;

  /**
   * The type of the identity. Valid values: - `UNKNOWN` - `USER` : Defines a single user. - `GROUP`
   * : Defines an existing group of identities within the indexed system. Individual members of this
   * group can be of any valid identity Type (USER, GROUP, or VIRTUAL_GROUP). - `VIRTUAL_GROUP` :
   * Defines a group that doesn't exist within the indexed system. Mechanically, a `VIRTUAL_GROUP`
   * is identical to a `GROUP`.
   */
  public SecurityIdentityType identityType;

  /**
   * The security identity provider through which the security identity is updated.
   *
   * <p>Defaults to the first security identity provider associated with the target Push source.
   */
  public String securityProvider;

  public SecurityIdentity(
      String identity, SecurityIdentityType securityIdentityType, String securityProvider) {
    this.identity = identity;
    this.identityType = securityIdentityType;
    this.securityProvider = securityProvider;
  }
}
