package com.coveo.pushapiclient;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserSecurityIdentityBuilderTest {

    @Test
    public void testSingleIdentity() {
        SecurityIdentity identityBuilt = new UserSecurityIdentityBuilder("bob@foo.com").build()[0];

        assertEquals("bob@foo.com", identityBuilt.identity);
        assertEquals(SecurityIdentityType.USER, identityBuilt.identityType);
        assertEquals("Email Security Provider", identityBuilt.securityProvider);

    }

    @Test
    public void testMultipleIdentities() {
        SecurityIdentity[] identitiesBuilt = new UserSecurityIdentityBuilder(new String[]{"bob@foo.com", "john@foo.com"}).build();

        assertEquals("bob@foo.com", identitiesBuilt[0].identity);
        assertEquals("john@foo.com", identitiesBuilt[1].identity);
    }

    @Test
    public void testSecurityProvider() {
        SecurityIdentity identityBuilt = new UserSecurityIdentityBuilder("bob@foo.com", "my provider").build()[0];

        assertEquals("my provider", identityBuilt.securityProvider);
    }
}