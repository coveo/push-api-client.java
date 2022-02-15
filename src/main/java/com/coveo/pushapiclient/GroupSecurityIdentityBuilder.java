package com.coveo.pushapiclient;

/**
 * Build a security identity of type `GROUP`.
 * <p>
 * Typically used in conjunction with {@link DocumentBuilder#withAllowedPermissions} or {@link DocumentBuilder#withDeniedPermissions}.
 * <p>
 * See {@link SecurityIdentity}.
 */
public class GroupSecurityIdentityBuilder implements SecurityIdentityBuilder {

    private String[] identities;
    private String securityProvider;

    public GroupSecurityIdentityBuilder(String[] identities, String securityProvider) {
        this.identities = identities;
        this.securityProvider = securityProvider;
    }

    /**
     * Construct a GroupSecurityIdentityBuilder with a single identity
     *
     * @param identity
     * @param securityProvider
     */
    public GroupSecurityIdentityBuilder(String identity, String securityProvider) {
        this(new String[]{identity}, securityProvider);
    }

    public SecurityIdentity[] build() {
        return new AnySecurityIdentityBuilder(this.identities, SecurityIdentityType.GROUP, this.securityProvider).build();
    }

    public String[] getIdentities() {
        return identities;
    }

    public String getSecurityProvider() {
        return securityProvider;
    }
}
