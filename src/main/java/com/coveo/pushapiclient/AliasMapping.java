package com.coveo.pushapiclient;

import java.util.Map;

public class AliasMapping extends IdentityModel {
    public String provider;

    public AliasMapping(String provider, String name, SecurityIdentityType type, Map<String, String> additionalInfo) {
        super(name, type, additionalInfo);
        this.provider = provider;
    }
}
