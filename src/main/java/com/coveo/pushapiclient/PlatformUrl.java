package com.coveo.pushapiclient;

public class PlatformUrl {

  public static final Environment DEFAULT_ENVIRONMENT = Environment.PRODUCTION;
  public static final Region DEFAULT_REGION = Region.US;

  private final Environment environment;
  private final Region region;

  /**
   * @param environment The environment platform of your organization
   * @param region The physical center of your organization
   * @see <a href="https://docs.coveo.com/en/2976">Deployment regions and strategies</a>
   */
  public PlatformUrl(Environment environment, Region region) {
    this.environment = environment;
    this.region = region;
  }

  public String getPlatformUrl() {
    return String.format(
        "https://platform%s%s.cloud.coveo.com", this.getUrlEnvironment(), this.getUrlRegion());
  }

  public String getApiUrl() {
    return String.format(
        "https://api%s%s.cloud.coveo.com", this.getUrlEnvironment(), this.getUrlRegion());
  }

  private String getUrlEnvironment() {
    return this.environment == PlatformUrl.DEFAULT_ENVIRONMENT ? "" : this.environment.getValue();
  }

  private String getUrlRegion() {
    return this.region == PlatformUrl.DEFAULT_REGION
        ? ""
        : String.format("-%s", this.region.getValue());
  }
}
