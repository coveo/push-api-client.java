package com.coveo.pushapiclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class UserSecurityIdentityBuilderTest {

  private UserSecurityIdentityBuilder usib1;
  private UserSecurityIdentityBuilder usib2;
  private UserSecurityIdentityBuilder usib3;
  private UserSecurityIdentityBuilder usib4;
  private UserSecurityIdentityBuilder usib5;

  @Before
  public void setUp() {
    String[] id1 = new String[] {"identity1", "identity2"};
    String[] id2 = new String[] {"identity2", "identity3"};

    usib1 = new UserSecurityIdentityBuilder(id1, "some_sec_provider");
    usib2 = new UserSecurityIdentityBuilder(id1, "some_sec_provider");
    usib3 = new UserSecurityIdentityBuilder(id2, "some_sec_provider");
    usib4 = new UserSecurityIdentityBuilder(id1, "some_other_sec_provider");
    usib5 = usib1;
  }

  @Test
  public void testSingleIdentity() {
    SecurityIdentity identityBuilt = new UserSecurityIdentityBuilder("bob@foo.com").build()[0];

    assertEquals("bob@foo.com", identityBuilt.identity);
    assertEquals(SecurityIdentityType.USER, identityBuilt.identityType);
    assertEquals("Email Security Provider", identityBuilt.securityProvider);
  }

  @Test
  public void testMultipleIdentities() {
    SecurityIdentity[] identitiesBuilt =
        new UserSecurityIdentityBuilder(new String[] {"bob@foo.com", "john@foo.com"}).build();

    assertEquals("bob@foo.com", identitiesBuilt[0].identity);
    assertEquals("john@foo.com", identitiesBuilt[1].identity);
  }

  @Test
  public void testSecurityProvider() {
    SecurityIdentity identityBuilt =
        new UserSecurityIdentityBuilder("bob@foo.com", "my provider").build()[0];

    assertEquals("my provider", identityBuilt.securityProvider);
  }

  @Test
  public void testToString() {
    assertEquals(usib1.toString(), usib1.toString());
    assertEquals(usib1.toString(), usib2.toString());
    assertEquals(usib1.toString(), usib5.toString());
    assertNotEquals(usib1.toString(), usib3.toString());
    assertNotEquals(usib3.toString(), usib4.toString());
  }

  @Test
  public void testEquals() {
    assertTrue(usib1.equals(usib1));
    assertTrue(usib1.equals(usib2));
    assertTrue(usib1.equals(usib5));
    assertFalse(usib3.equals(usib1));
    assertFalse(usib3.equals(usib4));
    assertFalse(usib1.equals(null));
  }

  @Test
  public void testHashCode() {
    assertEquals(usib1.hashCode(), usib1.hashCode());
    assertEquals(usib1.hashCode(), usib2.hashCode());
    assertEquals(usib1.hashCode(), usib5.hashCode());
    assertEquals(usib3.hashCode(), usib3.hashCode());
    assertNotEquals(usib1.hashCode(), usib3.hashCode());
    assertNotEquals(usib3.hashCode(), usib4.hashCode());
  }
}
