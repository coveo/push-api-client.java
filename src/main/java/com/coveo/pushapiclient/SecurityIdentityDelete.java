package com.coveo.pushapiclient;

/**
 * See [Disabling a Single Security Identity](https://docs.coveo.com/en/84)
 */
public class SecurityIdentityDelete {

    private IdentityModel identity;

    public SecurityIdentityDelete(IdentityModel identity) {
        this.identity = identity;
    }

    public IdentityModel getIdentity() {
        return identity;
    }
}
