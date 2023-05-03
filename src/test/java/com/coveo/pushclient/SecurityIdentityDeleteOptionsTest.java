package com.coveo.pushclient;

import com.coveo.security.SecurityIdentityDeleteOptions;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SecurityIdentityDeleteOptionsTest {

    private SecurityIdentityDeleteOptions opt1;
    private SecurityIdentityDeleteOptions opt2;
    private SecurityIdentityDeleteOptions opt3;
    private SecurityIdentityDeleteOptions opt4;
    private SecurityIdentityDeleteOptions opt5;

    @Before
    public void setUp() {
        opt1 = new SecurityIdentityDeleteOptions(123, 123L);
        opt2 = new SecurityIdentityDeleteOptions(123, 123L);
        opt3 = new SecurityIdentityDeleteOptions(456, 123L);
        opt4 = new SecurityIdentityDeleteOptions(123, 456L);
        opt5 = opt1;
    }

    @Test
    public void testToString() {
        assertEquals(opt1.toString(), opt1.toString());
        assertEquals(opt1.toString(), opt2.toString());
        assertEquals(opt1.toString(), opt5.toString());
        assertEquals(opt3.toString(), opt3.toString());
        assertNotEquals(opt1.toString(), opt3.toString());
        assertNotEquals(opt3.toString(), opt4.toString());
    }

    @Test
    public void testEquals() {
        assertTrue(opt1.equals(opt1));
        assertTrue(opt1.equals(opt2));
        assertTrue(opt1.equals(opt5));
        assertFalse(opt1.equals(opt3));
        assertFalse(opt1.equals(opt4));
        assertFalse(opt3.equals(opt4));
        assertFalse(opt1.equals(null));
    }

    @Test
    public void testHashCode() {
        assertEquals(opt1.hashCode(), opt1.hashCode());
        assertEquals(opt1.hashCode(), opt2.hashCode());
        assertEquals(opt1.hashCode(), opt5.hashCode());
        assertEquals(opt3.hashCode(), opt3.hashCode());
        assertEquals(opt4.hashCode(), opt4.hashCode());
        assertNotEquals(opt1.hashCode(), opt3.hashCode());
        assertNotEquals(opt1.hashCode(), opt4.hashCode());
        assertNotEquals(opt3.hashCode(), opt4.hashCode());
    }
}