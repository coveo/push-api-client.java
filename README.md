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
import com.coveo.document.DocumentBuilder;
import com.coveo.source.SourceClient;

import java.io.IOException;
import java.net.http.HttpResponse;

public class PushOneDocument {
    public static void main(String[] args) {
        SourceClient sourceClient = new SourceClient("my_api_key", "my_org_id");
        DocumentBuilder documentBuilder = new DocumentBuilder("https://my.document.uri", "My document title")
                .withData("these words will be searchable");

        try {
            HttpResponse<String> response = sourceClient.addOrUpdateDocument("my_source_id", documentBuilder);
            System.out.println(String.format("SourceClient creation status: %s", response.statusCode()));
            System.out.println(String.format("SourceClient creation response: %s", response.body()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

```

## Release

* Tag the commit following semver.
* Bump version in pom.xml
* mvn -P release clean deploy
* cd into ./target
* jar -cvf bundle.jar push-api-client.java-1.0.0-javadoc.jar push-api-client.java-1.0.0-javadoc.jar.asc push-api-client.java-1.0.0-sources.jar push-api-client.java-1.0.0-sources.jar.asc push-api-client.java-1.0.0.jar push-api-client.java-1.0.0.jar.asc push-api-client.java-1.0.0.pom push-api-client.java-1.0.0.pom.asc
* Log into https://oss.sonatype.org/
* Upload newly created bundle.jar
