# AGENTS.md

## Prerequisites

Install the following before running any project task:

- Java 11 or higher
- Apache Maven 3.6+
- (If installing from GitHub Packages) a GitHub personal access token with `read:packages` scope configured in `~/.m2/settings.xml`

## Build

Required installs: see [Prerequisites](#prerequisites).

Build command:

```bash
mvn -B package --file pom.xml
```

## Test

Required installs: see [Prerequisites](#prerequisites).

Test command:

```bash
mvn test
```

## Lint

Required installs: see [Prerequisites](#prerequisites).

Lint/format check command:

```bash
mvn spotless:check
```
