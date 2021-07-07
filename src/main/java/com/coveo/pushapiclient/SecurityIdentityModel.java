package com.coveo.pushapiclient;

/**
 * See [Security Identity Models](https://docs.coveo.com/en/139)
 */
public class SecurityIdentityModel extends SecurityIdentityModelBase {
    public final IdentityModel[] members;

    public SecurityIdentityModel(IdentityModel[] members, IdentityModel identity, IdentityModel[] wellKnowns) {
        super(identity, wellKnowns);
        this.members = members;
    }
}
