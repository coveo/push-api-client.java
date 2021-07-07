package com.coveo.pushapiclient;

public record VirtualGroupSecurityIdentityBuilder(String[] identities,
                                                  String securityProvider) implements SecurityIdentityBuilder {

    public VirtualGroupSecurityIdentityBuilder(String identity, String securityProvider) {
        this(new String[]{identity}, securityProvider);
    }

    public SecurityIdentity[] build() {
        return new AnySecurityIdentityBuilder(this.identities, SecurityIdentityType.VIRTUAL_GROUP, this.securityProvider).build();
    }
}

