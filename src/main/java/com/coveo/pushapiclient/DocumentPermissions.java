package com.coveo.pushapiclient;

public class DocumentPermissions {
  /** Whether to allow anonymous users in this permission set. Default value is false. */
  public boolean allowAnonymous;

  /** The list of allowed permissions for this permission set. */
  public SecurityIdentity[] allowedPermissions;

  /** The list of denied permissions for this permission set. */
  public SecurityIdentity[] deniedPermissions;

  public DocumentPermissions() {
    this.allowAnonymous = true;
    this.allowedPermissions = new SecurityIdentity[] {};
    this.deniedPermissions = new SecurityIdentity[] {};
  }
}
