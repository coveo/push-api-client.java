package com.coveo.pushapiclient;

public class SecurityIdentityModelBase {
  public final IdentityModel identity;
  public final IdentityModel[] wellKnowns;

  public SecurityIdentityModelBase(IdentityModel identity, IdentityModel[] wellKnowns) {
    this.identity = identity;
    this.wellKnowns = wellKnowns;
  }
}
