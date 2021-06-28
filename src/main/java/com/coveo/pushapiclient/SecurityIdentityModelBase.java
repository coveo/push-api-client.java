package com.coveo.pushapiclient;

public class SecurityIdentityModelBase {
    public IdentityModel identity;
    public IdentityModel[] wellKnowns;

    public SecurityIdentityModelBase(IdentityModel identity, IdentityModel[] wellKnowns) {
        this.identity = identity;
        this.wellKnowns = wellKnowns;
    }
}
