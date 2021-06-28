package com.coveo.pushapiclient;

import java.util.Map;

public class IdentityModel {
    public Map<String, String> additionalInfo;
    public String name;
    public SecurityIdentityType type;

    public IdentityModel(String name, SecurityIdentityType type, Map<String, String> additionalInfo) {
        this.name = name;
        this.type = type;
        this.additionalInfo = additionalInfo;
    }
}
