package com.coveo.pushapiclient;

public class SecurityIdentityModel extends SecurityIdentityModelBase {
    public IdentityModel[] members;

    public SecurityIdentityModel(IdentityModel[] members, IdentityModel identity, IdentityModel[] wellKnowns) {
        super(identity, wellKnowns);
        this.members = members;
    }
}
