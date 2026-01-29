# Stream Catalog API Update - Upgrade Notes

## Summary

The `UpdateStreamService` has been upgraded to align with the Coveo Catalog Stream API best practices for update operations. Previously, the service would create one file container and attempt to send multiple batches to it. Now, **each batch gets its own file container**, which is created, uploaded to, and immediately pushed to the stream source.

## What Changed

### Key Behavioral Changes

1. **File Container Lifecycle**: Each flush operation now follows the complete workflow:
   - Step 1: Create a new file container
   - Step 2: Upload batch content to the container  
   - Step 3: Push the container to the stream source via `/update` API
   - This happens automatically when the 256MB batch limit is exceeded or when `close()` is called

2. **Immediate Push**: File containers are pushed immediately after upload, not accumulated

3. **No Shared Containers**: Each batch uses a dedicated file container, preventing conflicts

### Modified Files

#### Core Implementation
- **`UpdateStreamServiceInternal.java`**
  - Removed file container creation from `add*()` methods
  - Added `createUploadAndPush()` method that handles the complete create→upload→push workflow
  - Modified `close()` to call `flushAndPush()` instead of `flush()`

- **`StreamDocumentUploadQueue.java`**
  - Added `flushAndPush()` method that creates a container, uploads, and pushes in one operation
  - Overrode all `add()` methods to call `flushAndPush()` when batch size is exceeded
  - Added reference to `UpdateStreamServiceInternal` to enable the complete workflow

- **`UpdateStreamService.java`**
  - Removed the `fileContainer` field (no longer needed)
  - Updated documentation to reflect new behavior
  - Removed unused `getUploadStrategy()` method

#### Tests
- **`UpdateStreamServiceInternalTest.java`**
  - Updated tests to verify file containers are created during flush, not during add
  - Added test for `createUploadAndPush()` method
  - Modified close tests to verify `flushAndPush()` is called

#### Samples
- **`UpdateStreamDocuments.java`**
  - Added documentation explaining the new file container behavior

## API Documentation Reference

This change implements the recommendations from the Coveo documentation:
- [Full Catalog Data Updates - Update Operations](https://docs.coveo.com/en/p4eb0129/coveo-for-commerce/full-catalog-data-updates#update-operations)

Key quote from the documentation:
> "Load operations require uploading and processing all file containers at once, which is resource-intensive and delays data availability until the entire load completes. In contrast, **update operations process each container as soon as it's ready**, allowing for faster indexing and more up-to-date catalog data throughout the update process."

## Migration Guide

### For Existing Users

The API remains the same - no code changes are required for basic usage:

```java
// UpdateStreamService - catalog updates with immediate push per batch
UpdateStreamService updateService = new UpdateStreamService(catalogSource);
updateService.addOrUpdate(document1);
updateService.close();

// PushService - push operations with immediate push per batch  
PushService pushService = new PushService(pushSource);
pushService.addOrUpdate(document1);
pushService.close();

// StreamService - load operations (collects batches before pushing)
StreamService streamService = new StreamService(streamSource);
streamService.open();
streamService.addOrUpdate(document1);
streamService.close();
```

### Configuring Batch Size

For detailed configuration options including runtime system property configuration (`coveo.push.batchSize`), see **[CONFIGURATION.md](CONFIGURATION.md)**.

If you need a smaller batch size (e.g., for more frequent pushes), you can specify it via constructor:

```java
// Set custom batch size (e.g., 50MB)
int customBatchSize = 50 * 1024 * 1024;

// UpdateStreamService
UpdateStreamService service = new UpdateStreamService(
    catalogSource, 
    backoffOptions, 
    userAgents, 
    customBatchSize
);

// PushService
PushService pushService = new PushService(
    pushSource, 
    backoffOptions, 
    customBatchSize
);

// StreamService
StreamService streamService = new StreamService(
    streamSource, 
    backoffOptions, 
    userAgents, 
    customBatchSize
);
```

**Note**: Batch size cannot exceed 256MB (Stream API limit). Attempting to set a larger value will throw an `IllegalArgumentException`.

### What You'll Notice

1. **Configurable Batch Size**: All services now support custom batch sizes via constructor parameter or system property (max: 256MB). Default remains 5MB.
   - `UpdateStreamService` (catalog updates)
   - `PushService` (push operations)  
   - `StreamService` (load operations)

2. **More API Calls for UpdateStreamService**: You may see more file container create and push operations in your logs, as each batch (when exceeding the batch size limit) now triggers a complete create→upload→push cycle

4. **Faster Indexing**: Documents will be available for indexing sooner, as they're pushed immediately rather than waiting for all batches to complete

5. **Better Alignment**: The behavior now matches the catalog stream API best practices for optimal performance

## Benefits

1. **Improved Performance**: Documents are processed as soon as each container is ready, rather than waiting for all uploads to complete

2. **Better Resource Utilization**: Avoids resource-intensive bulk operations

3. **Faster Data Availability**: Catalog data is indexed incrementally throughout the update process

4. **API Compliance**: Follows the recommended pattern from Coveo's official documentation

## Technical Details

### Before (Old Behavior)
```
Create file container once
→ Add batch 1 to container
→ Add batch 2 to container  
→ Add batch 3 to container
→ Push container once with all batches
```

### After (New Behavior)
```
Batch 1:
  → Create file container
  → Upload batch 1
  → Push container
  
Batch 2:
  → Create file container
  → Upload batch 2
  → Push container
  
Batch 3:
  → Create file container
  → Upload batch 3
  → Push container
```

Each batch is independent and processed immediately, following the update operation pattern described in the Coveo documentation.
