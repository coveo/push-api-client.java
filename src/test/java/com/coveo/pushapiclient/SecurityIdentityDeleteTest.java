package com.coveo.pushapiclient;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class SecurityIdentityDeleteTest {

    private SecurityIdentityDelete sid1;
    private SecurityIdentityDelete sid2;
    private SecurityIdentityDelete sid3;
    private SecurityIdentityDelete sid4;
    private SecurityIdentityDelete sid5;

    @Before
    public void setUp() {
        Map<String, String> info1 = new HashMap<>();
        Map<String, String> info2 = new HashMap<>();
        info1.put("key1", "value1");
        info1.put("key2", "value2");
        info2.put("key2.1", "value2.1");

        IdentityModel id1 = new IdentityModel("some_name", SecurityIdentityType.USER, info1);
        IdentityModel id3 = new IdentityModel("some_name", SecurityIdentityType.GROUP, info2);
        IdentityModel id4 = new IdentityModel("some_other_name", SecurityIdentityType.USER, null);

        sid1 = new SecurityIdentityDelete(id1);
        sid2 = new SecurityIdentityDelete(id1);
        sid3 = new SecurityIdentityDelete(id3);
        sid4 = new SecurityIdentityDelete(id4);
        sid5 = sid1;

    }

    @Test
    public void testToString() {
        assertEquals(sid1.toString(), sid1.toString());
        assertEquals(sid1.toString(), sid2.toString());
        assertEquals(sid1.toString(), sid5.toString());
        assertEquals(sid2.toString(), sid5.toString());
        assertEquals(sid3.toString(), sid3.toString());
        assertEquals(sid4.toString(), sid4.toString());
        assertNotEquals(sid1.toString(), sid3.toString());
        assertNotEquals(sid2.toString(), sid4.toString());
    }

    @Test
    public void testEquals() {
        assertTrue(sid1.equals(sid1));
        assertTrue(sid1.equals(sid2));
        assertTrue(sid1.equals(sid5));
        assertTrue(sid3.equals(sid3));
        assertFalse(sid1.equals(sid3));
        assertFalse(sid3.equals(sid4));
        assertFalse(sid1.equals(null));
    }

    @Test
    public void testHashCode() {
        assertEquals(sid1.hashCode(), sid1.hashCode());
        assertEquals(sid1.hashCode(), sid2.hashCode());
        assertEquals(sid1.hashCode(), sid5.hashCode());
        assertNotEquals(sid1.hashCode(), sid3.hashCode());
        assertNotEquals(sid3.hashCode(), sid4.hashCode());

    }
}