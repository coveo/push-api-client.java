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

1. Add your GitHub personal access token to install packages from GitHub Packages.

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
        Source source = new Source("my_api_key", "my_org_id");
        DocumentBuilder documentBuilder = new DocumentBuilder("https://my.document.uri", "My document title")
                .withData("these words will be searchable");

        try {
            HttpResponse<String> response = source.addOrUpdateDocument("my_source_id", documentBuilder);
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

## Logging

If you want to push multiple documents to your Coveo organization and use a service for that (e.g. `PushService`, `StreamService`), you need to configure a **logger** to be able to see what happens.

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

## Release

1. Tag the commit according to the [semantic versioning](https://semver.org/).

1. Bump version in `pom.xml`.

1. Run the following commands:

    1. `mvn -P release clean deploy`.

    1. `cd ./target`.

    1. `jar -cvf bundle.jar push-api-client.java-1.0.0-javadoc.jar push-api-client.java-1.0.0-javadoc.jar.asc push-api-client.java-1.0.0-sources.jar push-api-client.java-1.0.0-sources.jar.asc push-api-client.java-1.0.0.jar push-api-client.java-1.0.0.jar.asc push-api-client.java-1.0.0.pom push-api-client.java-1.0.0.pom.asc`

1. Log in to https://oss.sonatype.org/.

1. Upload the newly created `bundle.jar` file.
