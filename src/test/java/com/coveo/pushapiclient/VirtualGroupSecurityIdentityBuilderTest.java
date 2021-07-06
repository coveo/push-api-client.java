package com.coveo.pushapiclient;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VirtualGroupSecurityIdentityBuilderTest {
    @Test
    public void testSingleIdentity() {
        SecurityIdentity identityBuilt = new VirtualGroupSecurityIdentityBuilder("bob@foo.com", "my provider").build()[0];

        assertEquals("bob@foo.com", identityBuilt.identity);
        assertEquals(SecurityIdentityType.VIRTUAL_GROUP, identityBuilt.identityType);
        assertEquals("my provider", identityBuilt.securityProvider);

    }

    @Test
    public void testMultipleIdentities() {
        SecurityIdentity[] identitiesBuilt = new VirtualGroupSecurityIdentityBuilder(new String[]{"bob@foo.com", "john@foo.com"}, "my provider").build();

        assertEquals("bob@foo.com", identitiesBuilt[0].identity);
        assertEquals("john@foo.com", identitiesBuilt[1].identity);
    }
}