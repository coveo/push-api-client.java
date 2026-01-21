# Configuration Guide

This document describes the available configuration options for the Coveo Push API Java Client.

## Batch Size Configuration

The batch size controls how much data is accumulated before creating a file container and pushing to Coveo. The default is **256 MB** (matching the Stream API limit).

### Configuration Methods

There are two ways to configure the batch size:

#### 1. System Property (Runtime Configuration)

Set the `coveo.push.batchSize` system property to configure the default batch size globally for all service instances:

**Java Command Line:**
```bash
java -Dcoveo.push.batchSize=134217728 -jar your-application.jar
```

**Within Java Code:**
```java
// Set before creating any service instances
System.setProperty("coveo.push.batchSize", "134217728"); // 128 MB in bytes
```

**Maven/Gradle Build:**
```xml
<!-- pom.xml -->
<properties>
    <argLine>-Dcoveo.push.batchSize=134217728</argLine>
</properties>
```

```groovy
// build.gradle
test {
    systemProperty 'coveo.push.batchSize', '134217728'
}
```

**Example Values:**
- `268435456` = 256 MB (default)
- `134217728` = 128 MB
- `67108864` = 64 MB
- `33554432` = 32 MB
- `10485760` = 10 MB

#### 2. Constructor Parameter (Per-Instance Configuration)

Pass the `maxQueueSize` parameter when creating service instances:

```java
// UpdateStreamService with custom 128 MB batch size
UpdateStreamService service = new UpdateStreamService(
    catalogSource,
    backoffOptions,
    null,  // userAgents (optional)
    128 * 1024 * 1024  // 128 MB in bytes
);

// PushService with custom batch size
PushService pushService = new PushService(
    pushEnabledSource,
    backoffOptions,
    128 * 1024 * 1024  // 128 MB
);

// StreamService with custom batch size
StreamService streamService = new StreamService(
    streamEnabledSource,
    backoffOptions,
    null,  // userAgents (optional)
    128 * 1024 * 1024  // 128 MB
);
```

### Configuration Priority

When both methods are used:

1. **Constructor parameter** takes precedence (if specified)
2. **System property** is used as default (if set)
3. **Built-in default** of 256 MB is used otherwise

### Validation Rules

All batch size values are validated:

- ✅ **Maximum:** 256 MB (268,435,456 bytes) - API limit
- ✅ **Minimum:** Greater than 0
- ❌ Values exceeding 256 MB will throw `IllegalArgumentException`
- ❌ Invalid or negative values will throw `IllegalArgumentException`

### Examples

#### Example 1: Using System Property

```java
// Configure globally via system property
System.setProperty("coveo.push.batchSize", "134217728"); // 128 MB

// All services will use 128 MB by default
UpdateStreamService updateService = new UpdateStreamService(catalogSource, backoffOptions);
PushService pushService = new PushService(pushEnabledSource, backoffOptions);
StreamService streamService = new StreamService(streamEnabledSource, backoffOptions);
```

#### Example 2: Override Per Service

```java
// Set global default to 128 MB
System.setProperty("coveo.push.batchSize", "134217728");

// Update service uses global default (128 MB)
UpdateStreamService updateService = new UpdateStreamService(catalogSource, backoffOptions);

// Push service overrides with 64 MB
PushService pushService = new PushService(pushEnabledSource, backoffOptions, 64 * 1024 * 1024);

// Stream service uses global default (128 MB)
StreamService streamService = new StreamService(streamEnabledSource, backoffOptions);
```

#### Example 3: Docker/Container Environment

```yaml
# docker-compose.yml
services:
  app:
    image: your-app
    environment:
      - JAVA_OPTS=-Dcoveo.push.batchSize=134217728
```

#### Example 4: Kubernetes

```yaml
# deployment.yaml
apiVersion: v1
kind: Pod
metadata:
  name: coveo-pusher
spec:
  containers:
  - name: app
    image: your-app
    env:
    - name: JAVA_OPTS
      value: "-Dcoveo.push.batchSize=134217728"
```

### When to Adjust Batch Size

**Use smaller batches (32-64 MB) when:**
- Network bandwidth is limited
- Memory is constrained
- Processing many small documents
- You want more frequent progress updates

**Use larger batches (128-256 MB) when:**
- Network bandwidth is high
- Processing large documents or files
- You want to minimize API calls
- Maximum throughput is needed

**Keep default (256 MB) when:**
- You're unsure - it's optimized for most scenarios
- Processing mixed document sizes
- You want to maximize batch efficiency

### Configuration Property Reference

| Property Name | Description | Default Value | Valid Range |
|--------------|-------------|---------------|-------------|
| `coveo.push.batchSize` | Default batch size in bytes | `268435456` (256 MB) | 1 to 268435456 |

### Troubleshooting

**Error: "exceeds the Stream API limit of 268435456 bytes"**
- Your configured value is too large
- Maximum allowed is 256 MB (268,435,456 bytes)
- Reduce the configured value

**Error: "Invalid value for system property"**
- The value is not a valid integer
- Use numeric bytes value (e.g., `134217728` not `128MB`)

**Service uses default despite system property:**
- Ensure property is set before creating service instances
- Verify property name is exactly `coveo.push.batchSize`
- Check that constructor isn't explicitly passing a value

### Migration from Previous Versions

Previous versions used a hardcoded 5 MB limit. If you're upgrading:

**Option 1: Keep 5 MB behavior (not recommended)**
```java
System.setProperty("coveo.push.batchSize", "5242880"); // 5 MB
```

**Option 2: Use new 256 MB default (recommended)**
```java
// No configuration needed - automatic
UpdateStreamService service = new UpdateStreamService(catalogSource, backoffOptions);
```

**Option 3: Choose custom size**
```java
System.setProperty("coveo.push.batchSize", "67108864"); // 64 MB
```

See [UPGRADE_NOTES.md](UPGRADE_NOTES.md) for complete migration guidance.

## Additional Configuration

### API Client Configuration

See the main [README.md](README.md) for:
- Platform client setup
- Authentication configuration
- API endpoint URLs
- Retry and backoff options

### Environment Variables

The following environment variables can be used for general configuration:

- `COVEO_API_KEY` - API key for authentication
- `COVEO_ORGANIZATION_ID` - Organization identifier
- `COVEO_PLATFORM_URL` - Custom platform URL (if needed)

Refer to the Coveo Platform documentation for complete environment configuration options.
