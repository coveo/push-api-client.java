package com.coveo.pushapiclient;

/**
 * @see <a href="https://docs.coveo.com/en/46">User Alias Definition Examples</a>
 */
public class SecurityIdentityAliasModel extends SecurityIdentityModelBase {
  public final AliasMapping[] mappings;

  public SecurityIdentityAliasModel(
      AliasMapping[] mappings, IdentityModel identity, IdentityModel[] wellKnowns) {
    super(identity, wellKnowns);
    this.mappings = mappings;
  }
}
