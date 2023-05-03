package com.coveo.security;

import java.util.Arrays;

public class AnySecurityIdentityBuilder implements SecurityIdentityBuilder {
    private final String[] identities;
    private final SecurityIdentityType securityIdentityType;
    private final String securityProvider;

    public AnySecurityIdentityBuilder(String identity, SecurityIdentityType securityIdentityType, String securityProvider) {
        this.identities = new String[]{identity};
        this.securityIdentityType = securityIdentityType;
        this.securityProvider = securityProvider;
    }

    public AnySecurityIdentityBuilder(String[] identities, SecurityIdentityType securityIdentityType, String securityProvider) {
        this.identities = identities;
        this.securityIdentityType = securityIdentityType;
        this.securityProvider = securityProvider;
    }

    public SecurityIdentity[] build() {
        return Arrays.stream(this.identities)
                .map(identity -> new SecurityIdentity(identity, this.securityIdentityType, this.securityProvider))
                .toArray(SecurityIdentity[]::new);
    }
}
