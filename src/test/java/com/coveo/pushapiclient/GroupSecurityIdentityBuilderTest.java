package com.coveo.pushapiclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class GroupSecurityIdentityBuilderTest {

  private GroupSecurityIdentityBuilder gsib1;
  private GroupSecurityIdentityBuilder gsib2;
  private GroupSecurityIdentityBuilder gsib3;
  private GroupSecurityIdentityBuilder gsib4;
  private GroupSecurityIdentityBuilder gsib5;

  @Before
  public void setUp() {
    String[] id1 = new String[] {"identity1", "identity2"};
    String[] id2 = new String[] {"identity2", "identity3"};

    gsib1 = new GroupSecurityIdentityBuilder(id1, "some_sec_provider");
    gsib2 = new GroupSecurityIdentityBuilder(id1, "some_sec_provider");
    gsib3 = new GroupSecurityIdentityBuilder(id2, "some_sec_provider");
    gsib4 = new GroupSecurityIdentityBuilder(id1, "some_other_sec_provider");
    gsib5 = gsib1;
  }

  @Test
  public void testSingleIdentities() {
    SecurityIdentity identityBuilt =
        new GroupSecurityIdentityBuilder("bob@foo.com", "my provider").build()[0];
    assertEquals("bob@foo.com", identityBuilt.identity);
    assertEquals(SecurityIdentityType.GROUP, identityBuilt.identityType);
    assertEquals("my provider", identityBuilt.securityProvider);
  }

  @Test
  public void testMultipleIdentities() {
    SecurityIdentity[] identitiesBuilt =
        new GroupSecurityIdentityBuilder(
                new String[] {"bob@foo.com", "john@foo.com"}, "my provider")
            .build();

    assertEquals("bob@foo.com", identitiesBuilt[0].identity);
    assertEquals("john@foo.com", identitiesBuilt[1].identity);
  }

  @Test
  public void testToString() {
    assertEquals(gsib1.toString(), gsib1.toString());
    assertEquals(gsib1.toString(), gsib2.toString());
    assertEquals(gsib1.toString(), gsib5.toString());
    assertNotEquals(gsib1.toString(), gsib3.toString());
    assertNotEquals(gsib3.toString(), gsib4.toString());
  }

  @Test
  public void testEquals() {
    assertTrue(gsib1.equals(gsib1));
    assertTrue(gsib1.equals(gsib2));
    assertTrue(gsib1.equals(gsib5));
    assertFalse(gsib3.equals(gsib1));
    assertFalse(gsib3.equals(gsib4));
    assertFalse(gsib1.equals(null));
  }

  @Test
  public void testHashCode() {
    assertEquals(gsib1.hashCode(), gsib1.hashCode());
    assertEquals(gsib1.hashCode(), gsib2.hashCode());
    assertEquals(gsib1.hashCode(), gsib5.hashCode());
    assertEquals(gsib3.hashCode(), gsib3.hashCode());
    assertNotEquals(gsib1.hashCode(), gsib3.hashCode());
    assertNotEquals(gsib3.hashCode(), gsib4.hashCode());
  }
}
