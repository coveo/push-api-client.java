package com.coveo.pushapiclient;

public record GroupSecurityIdentityBuilder(String[] identities,
                                           String securityProvider) implements SecurityIdentityBuilder {

    public GroupSecurityIdentityBuilder(String identity, String securityProvider) {
        this(new String[]{identity}, securityProvider);
    }

    public SecurityIdentity[] build() {
        return new AnySecurityIdentityBuilder(this.identities, SecurityIdentityType.GROUP, this.securityProvider).build();
    }
}
