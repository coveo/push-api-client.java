package com.coveo.pushapiclient;

public record UserSecurityIdentityBuilder(String[] identities,
                                          String securityProvider) implements SecurityIdentityBuilder {

    public UserSecurityIdentityBuilder(String identity, String securityProvider) {
        this(new String[]{identity}, securityProvider);
    }

    public UserSecurityIdentityBuilder(String identity) {
        this(new String[]{identity}, "Email Security Provider");
    }

    public UserSecurityIdentityBuilder(String[] identities) {
        this(identities, "Email Security Provider");
    }

    public SecurityIdentity[] build() {
        return new AnySecurityIdentityBuilder(this.identities, SecurityIdentityType.USER, this.securityProvider).build();
    }
}
