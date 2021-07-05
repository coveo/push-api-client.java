package com.coveo.pushapiclient;

public class VirtualGroupSecurityIdentityBuilder implements SecurityIdentityBuilder {
    private final String[] identities;
    private final String securityProvider;

    public VirtualGroupSecurityIdentityBuilder(String identity, String securityProvider) {
        this.identities = new String[]{identity};
        this.securityProvider = securityProvider;

    }

    public VirtualGroupSecurityIdentityBuilder(String[] identities, String securityProvider) {
        this.identities = identities;
        this.securityProvider = securityProvider;
    }

    public SecurityIdentity[] build() {
        return new AnySecurityIdentityBuilder(this.identities, SecurityIdentityType.VIRTUAL_GROUP, this.securityProvider).build();
    }
}

