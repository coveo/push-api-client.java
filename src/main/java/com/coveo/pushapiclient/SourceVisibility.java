package com.coveo.pushapiclient;

/**
 * SourceVisibility controls the content security option that should be applied to the items in a
 * source. <a href="https://docs.coveo.com/en/1779/">Content Security</a>
 */
public enum SourceVisibility {
  /** Items can be accessed by the source owner only. */
  PRIVATE {
    public String toString() {
      return "PRIVATE";
    }
  },
  /** Items can be accessed by allowed users only. */
  SECURED {
    public String toString() {
      return "SECURED";
    }
  },
  /** Items can be accessed by any user. */
  SHARED {
    public String toString() {
      return "SHARED";
    }
  }
}
