# Push API Client

A [Coveo Push API](https://docs.coveo.com/en/12/api-reference/push-api) client library for Java.

## Prerequisites

The Coveo `push-api-client.java` package is stored on GitHub packages.
You will need a personal access token (classic) with at least `read:packages` scope to install this dependency.

For details, see [Authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages)

## Installation

### Step 1: Update `settings.xml`

You can install this GitHub Package with [Apache Maven](https://maven.apache.org/) by editing the `~/.m2/settings.xml` file:

1. Add a repository definition to the GitHub Package.

   ```xml
   <repository>
       <id>github</id>
       <url>https://maven.pkg.github.com/coveo/push-api-client.java</url>
       <snapshots>
           <enabled>true</enabled>
       </snapshots>
   </repository>
   ```

1. Add your GitHub personal access token with read:packages permission to install packages from GitHub Packages.

   ```xml
   <servers>
   <server>
       <id>github</id>
       <username>USERNAME</username>
       <password>TOKEN</password>
   </server>
   </servers>
   ```

### Step 2: Add a Coveo dependency to project

Add a Coveo dependency to your Maven project by editing the `pom.xml` file.

```xml
<dependency>
  <groupId>com.coveo</groupId>
  <artifactId>push-api-client.java</artifactId>
  <version>2.3.0</version>
</dependency>
```

### Step 3: Install the project files

To install the updated project files, build the Maven project.

```bash
mvn install
```

## Usage

> See more examples in the `./samples` folder.

```java
import com.coveo.pushapiclient.DocumentBuilder;
import com.coveo.pushapiclient.Source;

import java.io.IOException;
import java.net.http.HttpResponse;

public class PushOneDocument {
    public static void main(String[] args) {
        Source pushSource = new PushSource("my_api_key", "my_org_id");
        DocumentBuilder documentBuilder = new DocumentBuilder("https://my.document.uri", "My document title")
                .withData("these words will be searchable");

        try {
            HttpResponse<String> response = pushSource.addOrUpdateDocument("my_source_id", documentBuilder);
            System.out.println(String.format("Source creation status: %s", response.statusCode()));
            System.out.println(String.format("Source creation response: %s", response.body()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

```

### Exponential backoff retry configuration

By default, the SDK leverages an exponential backoff retry mechanism. Exponential backoff allows for the SDK to make multiple attempts to resolve throttled requests, increasing the amount of time to wait for each subsequent attempt. Outgoing requests will retry when a `429` status code is returned from the platform.

The exponential backoff parameters are as follows:

- `retryAfter` - The amount of time, in milliseconds, to wait between throttled request attempts.

  Optional, will default to 5,000.

- `maxRetries` - The maximum number of times to retry throttled requests.

  Optional, will default to 10.

- `timeMultiple` - The multiple by which to increase the wait time between each throttled request attempt.

  Optional, will default to 2.

You may configure the exponential backoff that will be applied to all outgoing requests. To do so, specify use the `BackoffOptionsBuilder` class to create a `BackoffOptions` object when creating either a `PushService`, `PushSource`, or `StreamService` object:

```java
PushSource pushSource = new PushSource("my_api_key", "my_org_id", new BackoffOptionsBuilder().withMaxRetries(5).withRetryAfter(10000).build());

PushService pushService = new PushService(myPushEnabledSource, new BackoffOptionsBuilder().withMaxRetries(10).build());

StreamService streamService = new StreamService(myStreamEnabledSource, new BackoffOptionsBuilder().withRetryAfter(2000).withTimeMultiple(3).build());
```

By default, requests will retry a maximum of 10 times, waiting 5 seconds after the first attempt, with a time multiple of 2 (which will equate to a maximum execution time of roughly 1.5 hours).

## Logging

If you want to push multiple documents to your Coveo organization and use a service for that (e.g. `PushService`, `StreamService`), you may find it useful to configure a **logger** to catch error and warning messages.

1. Go to your project's root folder.

1. Update the Apache Log4j2 configuration by editing the `log4j2.xml` file.
   The following example will print the log execution to the console.

   ```xml
   <?xml version="1.0" encoding="UTF-8"?>
   <Configuration>
       <Appenders>
           <Console name="ConsoleAppender" target="SYSTEM_OUT">
               <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n" />
           </Console>
       </Appenders>
       <Loggers>
           <Root level="debug">
               <AppenderRef ref="ConsoleAppender" />
           </Root>
       </Loggers>
   </Configuration>
   ```

   For more details, see [Log4j2 configuration](https://logging.apache.org/log4j/2.x/manual/configuration.html).

## Formatting the code before contributing

This project uses [Google Java Format](https://github.com/google/google-java-format), so make sure your code is properly formatted before opening a pull request.

To enforce code style and formatting rules, run the Maven Spotless plugin:

```bash
mvn spotless:apply
```

## Release Process

### Step 1 - Create a Release Pull Request

#### Step 1.a - Generate a JSON Web Token (JWT)

To initiate the release process, start by generating a JSON Web Token (JWT) using one of the provided scripts in [Generate a JSON Web Token (JWT)](https://docs.github.com/en/apps/creating-github-apps/authenticating-with-a-github-app/generating-a-json-web-token-jwt-for-a-github-app). Ensure to specify the path where the `developer-experience-bot` private key is stored (can be found in password manager).

#### Step 1.b - Obtain an Access Token

Next, obtain an access token by executing the following command, replacing `<JSON_TOKEN>` and `<RELEASER_INSTALLATION_ID>` with the JWT token created in the previous step and the respective releaser installation id.

```bash
curl -i -X POST \
    -H "Authorization: Bearer <JSON_TOKEN>" \
    -H "Accept: application/vnd.github.v3+json" \
    https://api.github.com/app/installations/<RELEASER_INSTALLATION_ID>/access_tokens
```

#### Step 1.c - Create a Release Pull Request

Using the access token generated in the previous step, create a release Pull Request. This PR will automatically include the appropriate version bump and update the changelog. Initially, run the command with the `--dry-run` flag to ensure the PR is created with the correct version.

```bash
release-please release-pr \
            --token=<ACCESS_TOKEN> \
            --repo-url=coveo/push-api-client.java \
            --release-type=maven \
            --target-branch=main \
```

In case the command creates a pull request with an incorrect tag, manually create a pull request and perform an empty commit as follows:

```bash
git commit --allow-empty -m "chore: release x.x.x" -m "Release-As: x.x.x"
```

This action will prompt `release-please` to bump to the desired version on the next run. Finally, merge the pull request and repeat Step 1.c.

### Step 2 - Tag the Commit

1. Tag the commit according to [semantic versioning](https://semver.org/) principles.

```bash
git tag -a vx.x.x <COMMIT_SHA> -m "chore(main): release x.x.x (#<PULL_REQUEST_NUMBER>)"
git push --tags
```

2. Merge the Pull Request.

### Step 3 - Manually Create a Release

Lastly, manually create a release on the [GitHub repository](https://github.com/coveo/push-api-client.java/releases). This action will trigger the package deploy workflow action.