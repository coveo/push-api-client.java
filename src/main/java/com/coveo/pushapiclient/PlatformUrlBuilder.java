package com.coveo.pushapiclient;

public class PlatformUrlBuilder {

  private Environment environment = PlatformUrl.DEFAULT_ENVIRONMENT;
  private Region region = PlatformUrl.DEFAULT_REGION;

  public PlatformUrlBuilder withEnvironment(Environment environment) {
    this.environment = environment;
    return this;
  }

  public PlatformUrlBuilder withRegion(Region region) {
    this.region = region;
    return this;
  }

  public PlatformUrl build() {
    return new PlatformUrl(this.environment, this.region);
  }
}
