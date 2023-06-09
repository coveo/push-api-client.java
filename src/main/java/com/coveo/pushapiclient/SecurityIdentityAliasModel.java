package com.coveo.pushapiclient;

/** See [User Alias Definition Examples](https://docs.coveo.com/en/46). */
public class SecurityIdentityAliasModel extends SecurityIdentityModelBase {
  public final AliasMapping[] mappings;

  public SecurityIdentityAliasModel(
      AliasMapping[] mappings, IdentityModel identity, IdentityModel[] wellKnowns) {
    super(identity, wellKnowns);
    this.mappings = mappings;
  }
}
