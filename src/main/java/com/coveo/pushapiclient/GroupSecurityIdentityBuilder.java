package com.coveo.pushapiclient;

import java.util.Arrays;
import java.util.Objects;

/**
 * Build a security identity of type `GROUP`.
 * <p>
 * Typically used in conjunction with {@link DocumentBuilder#withAllowedPermissions} or {@link DocumentBuilder#withDeniedPermissions}.
 * <p>
 * See {@link SecurityIdentity}.
 */
public class GroupSecurityIdentityBuilder implements SecurityIdentityBuilder {

    private final String[] identities;
    private final String securityProvider;

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

    @Override
    public String toString() {
        return "GroupSecurityIdentityBuilder[" +
                "identities=" + Arrays.toString(identities) +
                ", securityProvider='" + securityProvider + '\'' +
                ']';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        GroupSecurityIdentityBuilder that = (GroupSecurityIdentityBuilder) obj;
        return Arrays.equals(identities, that.identities) && Objects.equals(securityProvider, that.securityProvider);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(securityProvider);
        result = 31 * result + Arrays.hashCode(identities);
        return result;
    }
}
