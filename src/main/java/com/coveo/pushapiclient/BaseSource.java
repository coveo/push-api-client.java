package com.coveo.pushapiclient;

public interface BaseSource {
  /**
   * Returns the API key used for all operations regarding your source.
   *
   * @return
   */
  String getApiKey();

  /**
   * Returns the {@link PlatformUrl} object associated to the source.
   *
   * @return
   */
  PlatformUrl getPlatformUrl();

  /**
   * The unique identifier of your organization.
   *
   * @return
   */
  String getOrganizationId();

  /**
   * The unique identifier of your source.
   *
   * @return
   */
  String getId();
}
