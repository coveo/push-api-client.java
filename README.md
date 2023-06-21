# Push API Client

A Coveo Push API Client in Java

## Installation

### Step 1: Prerequisites
The Coveo `push-api-client.java` package is stored on Github packages. You will need a personal access token (classic) with at least `read:packages` scope to install this dependency.

More info, visit [Authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages)

### Step 2: Update `settings.xml`
You can install this GitHub Package with Apache Maven by editing your `~/.m2/settings.xml`:

#### The repository to the package
Add a definition to the Github Package

```xml
<repository>
  <id>github</id>
  <url>https://maven.pkg.github.com/coveo/push-api-client.java</url>
  <snapshots>
    <enabled>true</enabled>
  </snapshots>
</repository>
```

#### Your GitHub personal access token
Your personal access token required to install packages from Github Packages

```xml
<servers>
  <server>
    <id>github</id>
    <username>USERNAME</username>
    <password>TOKEN</password>
  </server>
</servers>
```

### Step 3: Add Coveo dependency to project
Using Maven:


Add Coveo dependency to your maven project. Instructions on how to do that available in this [URL](https://github.com/coveo/push-api-client.java/packages/1884180).

### Step 4: Install
Run via command line
```bash
mvn install
```

## Usage

See more examples in the `./samples` folder.

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

## Local Setup to Contribute

### Formatting

This project uses [Google Java Format](https://github.com/google/google-java-format), so make sure your code is properly formatted before opening a pull request.
```bash
mvn spotless:apply
```

## Release

* Tag the commit following semver.
* Bump version in pom.xml
* mvn -P release clean deploy
* cd into ./target
* jar -cvf bundle.jar push-api-client.java-1.0.0-javadoc.jar push-api-client.java-1.0.0-javadoc.jar.asc push-api-client.java-1.0.0-sources.jar push-api-client.java-1.0.0-sources.jar.asc push-api-client.java-1.0.0.jar push-api-client.java-1.0.0.jar.asc push-api-client.java-1.0.0.pom push-api-client.java-1.0.0.pom.asc
* Log into https://oss.sonatype.org/
* Upload newly created bundle.jar
