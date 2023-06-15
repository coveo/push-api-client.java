package com.coveo.pushapiclient;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Private util class to extract dynamic parts from a API URL Handles extraction of identifiers and
 * platform URL from a source URL.
 *
 * @see <a href="https://docs.coveo.com/en/1546#push-api-url">Push API url</a>
 * @see <a href="https://docs.coveo.com/en/3295#stream-api-url">Stream API url</a>
 */
class ApiUrl {
  private final String organizationId;
  private final String sourceId;
  private final PlatformUrl platformUrl;
  private final String sourceUrl;

  public ApiUrl(URL sourceUrl) throws MalformedURLException {
    List<String> identifiers = this.extractIdentifiers(sourceUrl);
    this.organizationId = identifiers.get(0);
    this.sourceId = identifiers.get(1);
    this.sourceUrl = sourceUrl.toString();
    this.platformUrl = this.extractPlatformUrl(sourceUrl);
  }

  public ApiUrl(String organizationId, String sourceId, PlatformUrl platformUrl) {
    this.organizationId = organizationId;
    this.sourceId = sourceId;
    this.platformUrl = platformUrl;
    this.sourceUrl =
        String.format(
            "https://api.cloud.coveo.com/push/v1/organizations/%s/sources/%s",
            this.organizationId, this.sourceId);
  }

  public String getUrl() {
    return this.sourceUrl;
  }

  public String getOrganizationId() {
    return this.organizationId;
  }

  public String getSourceId() {
    return this.sourceId;
  }

  public PlatformUrl getPlatformUrl() {
    return this.platformUrl;
  }

  private List<String> extractIdentifiers(URL sourceUrl) throws MalformedURLException {
    String host = sourceUrl.getPath();
    Pattern pattern = Pattern.compile("/push/v1/organizations/([^/]+)/sources/([^/]+)");
    Matcher matcher = pattern.matcher(host);

    if (matcher.find()) {
      String organizationId = matcher.group(1);
      String sourceId = matcher.group(2);
      return Arrays.asList(organizationId, sourceId);
    }

    String errorMessage =
        this.getErrorMessage(
            "Unable to find organization and source ids from the provided API url");
    throw new MalformedURLException(errorMessage);
  }

  private PlatformUrl extractPlatformUrl(URL sourceUrl) throws MalformedURLException {
    String host = sourceUrl.getHost();
    Pattern pattern = Pattern.compile("api([a-z]*)([a-z-]*)\\.cloud\\.coveo\\.com");
    Matcher matcher = pattern.matcher(host);

    if (matcher.find()) {
      String extractedEnvironment = matcher.group(1);
      String extractedRegion = matcher.group(2).replace("-", "");

      Environment urlEnvironment =
          extractedEnvironment.isEmpty()
              ? PlatformUrl.DEFAULT_ENVIRONMENT
              : EnumSet.allOf(Environment.class).stream()
                  .filter(e -> e.getValue().equalsIgnoreCase(extractedEnvironment))
                  .findFirst()
                  .orElseThrow(
                      () ->
                          new MalformedURLException(
                              String.format(
                                  "Invalid platform environment '%s'", extractedEnvironment)));

      Region urlRegion =
          extractedRegion.isEmpty()
              ? PlatformUrl.DEFAULT_REGION
              : EnumSet.allOf(Region.class).stream()
                  .filter(r -> r.getValue().equalsIgnoreCase(extractedRegion))
                  .findFirst()
                  .orElseThrow(
                      () ->
                          new MalformedURLException(
                              String.format("Invalid platform region '%s'", extractedRegion)));

      return new PlatformUrl(urlEnvironment, urlRegion);
    }

    String invalidHostMessage = this.getErrorMessage("Invalid API URL host");
    throw new MalformedURLException(invalidHostMessage);
  }

  private String getErrorMessage(String reason) {
    String newLine = System.getProperty("line.separator");
    String message = "The provided API URL is invalid";

    message.concat(newLine).concat(reason);

    message
        .concat(newLine)
        .concat("For a Push Source, visit: https://docs.coveo.com/en/1546")
        .concat(newLine)
        .concat("For a Catalog Source, visit:https://docs.coveo.com/en/3295");

    return message;
  }
}
