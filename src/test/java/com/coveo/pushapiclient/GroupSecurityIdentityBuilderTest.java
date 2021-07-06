package com.coveo.pushapiclient;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GroupSecurityIdentityBuilderTest {
    @Test
    public void testSingleIdentities() {
        SecurityIdentity identityBuilt = new GroupSecurityIdentityBuilder("bob@foo.com", "my provider").build()[0];
        assertEquals("bob@foo.com", identityBuilt.identity);
        assertEquals(SecurityIdentityType.GROUP, identityBuilt.identityType);
        assertEquals("my provider", identityBuilt.securityProvider);
    }

    @Test
    public void testMultipleIdentities() {
        SecurityIdentity[] identitiesBuilt = new GroupSecurityIdentityBuilder(new String[]{"bob@foo.com", "john@foo.com"}, "my provider").build();

        assertEquals("bob@foo.com", identitiesBuilt[0].identity);
        assertEquals("john@foo.com", identitiesBuilt[1].identity);

    }
}