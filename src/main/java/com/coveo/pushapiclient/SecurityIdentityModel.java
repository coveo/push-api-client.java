package com.coveo.pushapiclient;

/**
 * @see <a href="https://docs.coveo.com/en/139">Security Identity Models</a>
 */
public class SecurityIdentityModel extends SecurityIdentityModelBase {
  public final IdentityModel[] members;

  public SecurityIdentityModel(
      IdentityModel[] members, IdentityModel identity, IdentityModel[] wellKnowns) {
    super(identity, wellKnowns);
    this.members = members;
  }
}
