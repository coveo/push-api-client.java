package com.coveo.pushapiclient;

public class GroupSecurityIdentityBuilder implements SecurityIdentityBuilder {
    private final String[] identities;
    private final String securityProvider;

    public GroupSecurityIdentityBuilder(String identity, String securityProvider) {
        this.identities = new String[]{identity};
        this.securityProvider = securityProvider;

    }

    public GroupSecurityIdentityBuilder(String[] identities, String securityProvider) {
        this.identities = identities;
        this.securityProvider = securityProvider;
    }

    public SecurityIdentity[] build() {
        return new AnySecurityIdentityBuilder(this.identities, SecurityIdentityType.GROUP, this.securityProvider).build();
    }
}
