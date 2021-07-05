package com.coveo.pushapiclient;

public class SecurityIdentityAliasModel extends SecurityIdentityModelBase {
    public final AliasMapping[] mappings;

    public SecurityIdentityAliasModel(AliasMapping[] mappings, IdentityModel identity, IdentityModel[] wellKnowns) {
        super(identity, wellKnowns);
        this.mappings = mappings;
    }
}
