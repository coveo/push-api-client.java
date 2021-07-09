package com.coveo.pushapiclient;

/**
 * Build a security identity of type `VIRTUAL_GROUP`.
 * <p>
 * Typically used in conjunction with {@link DocumentBuilder#withAllowedPermissions} or {@link DocumentBuilder#withDeniedPermissions}.
 * <p>
 * See {@link SecurityIdentity}.
 */
public record VirtualGroupSecurityIdentityBuilder(String[] identities,
                                                  String securityProvider) implements SecurityIdentityBuilder {

    /**
     * Construct a VirtualGroupSecurityIdentityBuilder with a single identity.
     *
     * @param identity
     * @param securityProvider
     */
    public VirtualGroupSecurityIdentityBuilder(String identity, String securityProvider) {
        this(new String[]{identity}, securityProvider);
    }

    public SecurityIdentity[] build() {
        return new AnySecurityIdentityBuilder(this.identities, SecurityIdentityType.VIRTUAL_GROUP, this.securityProvider).build();
    }
}

