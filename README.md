# Push API Client

A Coveo Push API Client in Java

## Installation

Using Maven:

```
<dependency>
  <groupId>com.coveo</groupId>
  <artifactId>push-api-client.java</artifactId>
  <version>2.2.0</version>
</dependency>
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
