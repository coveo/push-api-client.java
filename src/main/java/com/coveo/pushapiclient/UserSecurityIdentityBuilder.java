package com.coveo.pushapiclient;

/**
 * Build a security identity of type `USER`.
 * <p>
 * Typically used in conjunction with {@link DocumentBuilder.withAllowedPermissions} or {@link DocumentBuilder.withDeniedPermissions}.
 * <p>
 * See {@link SecurityIdentity}.
 */
public record UserSecurityIdentityBuilder(String[] identities,
                                          String securityProvider) implements SecurityIdentityBuilder {

    /**
     * Construct a UserSecurityIdentityBuilder for a single identity with the given security provider.
     *
     * @param identity
     * @param securityProvider
     */
    public UserSecurityIdentityBuilder(String identity, String securityProvider) {
        this(new String[]{identity}, securityProvider);
    }

    /**
     * Construct a UserSecurityIdentityBuilder for a single identity with an `Email Security Provider`.
     *
     * @param identity
     */
    public UserSecurityIdentityBuilder(String identity) {
        this(new String[]{identity}, "Email Security Provider");
    }

    /**
     * Construct a UserSecurityIdentityBuilder for multiple identities with an `Email Security Provider`.
     *
     * @param identities
     */
    public UserSecurityIdentityBuilder(String[] identities) {
        this(identities, "Email Security Provider");
    }


    public SecurityIdentity[] build() {
        return new AnySecurityIdentityBuilder(this.identities, SecurityIdentityType.USER, this.securityProvider).build();
    }
}
