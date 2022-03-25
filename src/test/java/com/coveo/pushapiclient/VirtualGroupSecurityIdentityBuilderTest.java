package com.coveo.pushapiclient;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class VirtualGroupSecurityIdentityBuilderTest {

    private VirtualGroupSecurityIdentityBuilder vgsib1;
    private VirtualGroupSecurityIdentityBuilder vgsib2;
    private VirtualGroupSecurityIdentityBuilder vgsib3;
    private VirtualGroupSecurityIdentityBuilder vgsib4;
    private VirtualGroupSecurityIdentityBuilder vgsib5;

    @Before
    public void setUp() {
        String[] id1 = new String[]{"identity1", "identity2"};
        String[] id2 = new String[]{"identity2", "identity3"};

        vgsib1 = new VirtualGroupSecurityIdentityBuilder(id1, "some_sec_provider");
        vgsib2 = new VirtualGroupSecurityIdentityBuilder(id1, "some_sec_provider");
        vgsib3 = new VirtualGroupSecurityIdentityBuilder(id2, "some_sec_provider");
        vgsib4 = new VirtualGroupSecurityIdentityBuilder(id1, "some_other_sec_provider");
        vgsib5 = vgsib1;
    }
    
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

    @Test
    public void testToString() {
        assertEquals(vgsib1.toString(), vgsib1.toString());
        assertEquals(vgsib1.toString(), vgsib2.toString());
        assertEquals(vgsib1.toString(), vgsib5.toString());
        assertNotEquals(vgsib1.toString(), vgsib3.toString());
        assertNotEquals(vgsib3.toString(), vgsib4.toString());
    }

    @Test
    public void testEquals() {
        assertTrue(vgsib1.equals(vgsib1));
        assertTrue(vgsib1.equals(vgsib2));
        assertTrue(vgsib1.equals(vgsib5));
        assertFalse(vgsib3.equals(vgsib1));
        assertFalse(vgsib3.equals(vgsib4));
        assertFalse(vgsib1.equals(null));
    }

    @Test
    public void testHashCode() {
        assertEquals(vgsib1.hashCode(), vgsib1.hashCode());
        assertEquals(vgsib1.hashCode(), vgsib2.hashCode());
        assertEquals(vgsib1.hashCode(), vgsib5.hashCode());
        assertEquals(vgsib3.hashCode(), vgsib3.hashCode());
        assertNotEquals(vgsib1.hashCode(), vgsib3.hashCode());
        assertNotEquals(vgsib3.hashCode(), vgsib4.hashCode());
    }
}