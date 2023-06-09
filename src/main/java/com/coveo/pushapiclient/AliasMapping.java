package com.coveo.pushapiclient;

import java.util.Map;

public class AliasMapping extends IdentityModel {
  public final String provider;

  public AliasMapping(
      String provider, String name, SecurityIdentityType type, Map<String, String> additionalInfo) {
    super(name, type, additionalInfo);
    this.provider = provider;
  }
}
