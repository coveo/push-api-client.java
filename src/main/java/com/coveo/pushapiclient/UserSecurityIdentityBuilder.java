package com.coveo.pushapiclient;

public class UserSecurityIdentityBuilder implements SecurityIdentityBuilder {
    private final String[] identities;
    private final String securityProvider;

    public UserSecurityIdentityBuilder(String identity, String securityProvider) {
        this.identities = new String[]{identity};
        this.securityProvider = securityProvider;

    }

    public UserSecurityIdentityBuilder(String identity) {
        this.identities = new String[]{identity};
        this.securityProvider = "Email Security Provider";

    }

    public UserSecurityIdentityBuilder(String[] identities, String securityProvider) {
        this.identities = identities;
        this.securityProvider = securityProvider;
    }

    public UserSecurityIdentityBuilder(String[] identities) {
        this.identities = identities;
        this.securityProvider = "Email Security Provider";
    }

    public SecurityIdentity[] build() {
        return new AnySecurityIdentityBuilder(this.identities, SecurityIdentityType.USER, this.securityProvider).build();
    }
}
