# PR #186 Split Plan: Configurable Batch Size Feature

## TL;DR

> **Quick Summary**: Split PR #186 (1368 additions, 136 deletions, 16 files) into 3 smaller, independently mergeable PRs that introduce configurable batch sizing and file container rotation for the Coveo Push API Java Client.
> 
> **Deliverables**:
> - PR1: Independent bugfix for null userAgents validation
> - PR2: Batch size configuration infrastructure in DocumentUploadQueue
> - PR3: Service-level integration with configurable batch size + file container rotation + documentation
> 
> **Estimated Effort**: Medium (PR extraction from existing branch)
> **Parallel Execution**: NO - sequential dependency chain
> **Critical Path**: PR1 → PR2 → PR3

---

## Context

### Original Request
Split PR #186 ("feat: increase batch size to 256MB and add configurable batching with runtime system property") into smaller, independently mergeable PRs.

### Interview Summary
**Key Decisions**:
- User chose 3-PR split (consolidated from initial 4-PR proposal)
- Documentation bundled with code features
- PlatformClient null check is independent bugfix (PR1)
- UpdateStreamService cannot be split from behavioral changes (tight coupling)

**Technical Constraints Identified**:
- `StreamDocumentUploadQueue.flushAndPush()` calls `UpdateStreamServiceInternal.createUploadAndPush()` - must be in same PR
- UpdateStreamService's new constructor passes `null` as UploadStrategy - requires behavioral changes to compile
- Each PR must compile and pass tests independently

### Source Branch
- Branch: `feature/configurable-batch-size-256mb`
- Commits: 6 total (45282f1, 8280d47, 68e46e1, 6d05dfe, 66b789e, 682bb0d)

---

## Work Objectives

### Core Objective
Extract changes from a single large PR into 3 smaller PRs that can be reviewed and merged independently while maintaining build stability.

### Concrete Deliverables
- 3 new branches created from `main`
- 3 pull requests with appropriate scope
- Each PR passes `mvn test` independently
- Each PR follows conventional commit format

### Definition of Done
- [ ] All 3 PRs created and passing CI
- [ ] PR1 merged before PR2, PR2 merged before PR3
- [ ] Original PR #186 can be closed after PR3 merges
- [ ] `mvn test` passes after each PR merge

### Must Have
- Each PR compiles independently
- Tests accompany their corresponding code changes
- Clear dependency chain documented in PR descriptions

### Must NOT Have (Guardrails)
- Do NOT cherry-pick individual commits (they contain mixed changes)
- Do NOT modify logic beyond what's in the original PR
- Do NOT reorder the dependency chain (PR1 → PR2 → PR3)
- Do NOT include documentation in PR1 or PR2 (all docs go in PR3)

---

## Verification Strategy

### Test Decision
- **Infrastructure exists**: YES (JUnit 4 + Mockito)
- **Test approach**: Tests accompany code changes
- **Framework**: Maven with `mvn test`

### Verification Commands
```bash
# After each PR's changes are applied:
mvn clean compile          # Must succeed
mvn test                   # Must pass all tests
mvn spotless:check         # Must pass formatting
```

---

## Execution Strategy

### Dependency Chain (Sequential)

```
PR1: fix(PlatformClient): validate userAgents is not null
  │
  │ MERGE PR1
  ▼
PR2: feat(DocumentUploadQueue): add configurable batch size infrastructure
  │
  │ MERGE PR2
  ▼
PR3: feat(services): add configurable batch size with file container rotation
```

### Why Sequential (Not Parallel)
- PR2 depends on PR1 for the `userAgents != null` check pattern
- PR3 depends on PR2 for `DocumentUploadQueue.getConfiguredBatchSize()` and `DEFAULT_QUEUE_SIZE`
- Cannot parallelize without creating merge conflicts

---

## TODOs

---

### TODO 1: Create PR1 - PlatformClient Null Check Fix

- [ ] 1. Create PR1: `fix(PlatformClient): validate userAgents is not null`

**What to do**:

1. Create new branch `fix/platform-client-null-userAgents` from `main`
2. Extract ONLY the null check addition from `PlatformClient.java`
3. Create PR with minimal scope

**Files to include**:

| File | Change Type | Specific Changes |
|------|-------------|------------------|
| `src/main/java/com/coveo/pushapiclient/PlatformClient.java` | MODIFY | Add null check in `setUserAgents()` method (3 lines) |

**Exact change to extract** (lines to add at `setUserAgents()` method, before existing validation):
```java
public void setUserAgents(String[] userAgents) {
    if (userAgents == null) {
      throw new IllegalArgumentException("User agents cannot be null");
    }
    // ... existing validation continues
}
```

**Must NOT do**:
- Do NOT include any other PlatformClient changes
- Do NOT include any test changes (this is a defensive fix)
- Do NOT include any documentation

**PR Title**: `fix(PlatformClient): validate userAgents is not null`

**PR Description**:
```markdown
## Summary
Adds null validation to `PlatformClient.setUserAgents()` to fail fast with a clear error message instead of potentially causing NullPointerException downstream.

## Changes
- Added null check that throws `IllegalArgumentException` if `userAgents` is null

## Testing
- Existing tests pass
- This is a defensive fix; callers should not pass null
```

**Dependencies**: None (can merge to `main` directly)

**Recommended Agent Profile**:
- **Category**: `quick`
- **Skills**: [`git-master`]
  - `git-master`: Needed for precise git operations (cherry-pick partial changes, create branch)

**Implementation Approach**:
```bash
# 1. Create branch from main
git checkout main
git pull origin main
git checkout -b fix/platform-client-null-userAgents

# 2. Extract ONLY the PlatformClient change
# Use git show to view the change, then manually apply just those 3 lines
git show 66b789e:src/main/java/com/coveo/pushapiclient/PlatformClient.java > /tmp/new.java
# Compare and extract only the null check addition

# 3. Alternative: manual edit
# Add these 3 lines to setUserAgents() method:
#   if (userAgents == null) {
#     throw new IllegalArgumentException("User agents cannot be null");
#   }

# 4. Verify
mvn clean compile
mvn test

# 5. Commit and push
git add src/main/java/com/coveo/pushapiclient/PlatformClient.java
git commit -m "fix(PlatformClient): validate userAgents is not null"
git push -u origin fix/platform-client-null-userAgents

# 6. Create PR via gh CLI
gh pr create --title "fix(PlatformClient): validate userAgents is not null" \
  --body "## Summary
Adds null validation to \`PlatformClient.setUserAgents()\` to fail fast with a clear error message.

## Changes
- Added null check that throws \`IllegalArgumentException\` if \`userAgents\` is null

## Testing
- Existing tests pass"
```

**Acceptance Criteria**:
- [ ] `mvn clean compile` succeeds
- [ ] `mvn test` passes (no new test failures)
- [ ] `mvn spotless:check` passes
- [ ] PR contains exactly 1 file changed, +3 lines
- [ ] Calling `setUserAgents(null)` throws `IllegalArgumentException`

**Commit**: YES
- Message: `fix(PlatformClient): validate userAgents is not null`
- Files: `src/main/java/com/coveo/pushapiclient/PlatformClient.java`
- Pre-commit: `mvn test`

---

### TODO 2: Create PR2 - Batch Size Infrastructure

- [ ] 2. Create PR2: `feat(DocumentUploadQueue): add configurable batch size infrastructure`

**What to do**:

1. Create new branch `feat/configurable-batch-size-infrastructure` from `main` (after PR1 merges)
2. Extract DocumentUploadQueue changes (constants, static method, new constructor)
3. Extract corresponding test updates
4. Ensure backward compatibility (existing constructor still works)

**Files to include**:

| File | Change Type | Specific Changes |
|------|-------------|------------------|
| `src/main/java/com/coveo/pushapiclient/DocumentUploadQueue.java` | MODIFY | Add constants, `getConfiguredBatchSize()`, new constructor with validation |
| `src/test/java/com/coveo/pushapiclient/DocumentUploadQueueTest.java` | MODIFY | Update tests to use explicit batch size |

**Exact changes for DocumentUploadQueue.java**:

1. **Add constants** (after logger declaration):
```java
/** Maximum allowed queue size based on Stream API limit (256 MB) */
protected static final int MAX_ALLOWED_QUEUE_SIZE = 256 * 1024 * 1024;

/** Default queue size (256 MB to match API limit) */
protected static final int DEFAULT_QUEUE_SIZE = 256 * 1024 * 1024;

/** System property name for configuring the default batch size */
public static final String BATCH_SIZE_PROPERTY = "coveo.push.batchSize";
```

2. **Add field**:
```java
protected final int maxQueueSize;
```

3. **Add static method** `getConfiguredBatchSize()`:
```java
/**
 * Gets the configured batch size from system properties, or returns the default if not set.
 * 
 * @return The configured batch size in bytes
 * @throws IllegalArgumentException if the configured value exceeds 256MB or is invalid
 */
public static int getConfiguredBatchSize() {
  String propertyValue = System.getProperty(BATCH_SIZE_PROPERTY);
  if (propertyValue == null || propertyValue.trim().isEmpty()) {
    return DEFAULT_QUEUE_SIZE;
  }
  
  try {
    int configuredSize = Integer.parseInt(propertyValue.trim());
    if (configuredSize > MAX_ALLOWED_QUEUE_SIZE) {
      throw new IllegalArgumentException(
          String.format("System property %s (%d bytes) exceeds the Stream API limit of %d bytes (256 MB)",
              BATCH_SIZE_PROPERTY, configuredSize, MAX_ALLOWED_QUEUE_SIZE));
    }
    if (configuredSize <= 0) {
      throw new IllegalArgumentException(
          String.format("System property %s must be greater than 0, got: %d",
              BATCH_SIZE_PROPERTY, configuredSize));
    }
    logger.info(String.format("Using configured batch size from system property %s: %d bytes (%.2f MB)",
        BATCH_SIZE_PROPERTY, configuredSize, configuredSize / (1024.0 * 1024.0)));
    return configuredSize;
  } catch (NumberFormatException e) {
    throw new IllegalArgumentException(
        String.format("Invalid value for system property %s: '%s'. Must be a valid integer.",
            BATCH_SIZE_PROPERTY, propertyValue), e);
  }
}
```

4. **Modify existing constructor** to delegate:
```java
public DocumentUploadQueue(UploadStrategy uploader) {
  this(uploader, getConfiguredBatchSize());
}
```

5. **Add new constructor** with maxQueueSize parameter:
```java
/**
 * Constructs a new DocumentUploadQueue object with a configurable maximum queue size limit.
 *
 * @param uploader The upload strategy to be used for document uploads.
 * @param maxQueueSize The maximum queue size in bytes. Must not exceed 256MB (Stream API limit).
 * @throws IllegalArgumentException if maxQueueSize exceeds the API limit of 256MB.
 */
public DocumentUploadQueue(UploadStrategy uploader, int maxQueueSize) {
  if (maxQueueSize > MAX_ALLOWED_QUEUE_SIZE) {
    throw new IllegalArgumentException(
        String.format("maxQueueSize (%d bytes) exceeds the Stream API limit of %d bytes (256 MB)",
            maxQueueSize, MAX_ALLOWED_QUEUE_SIZE));
  }
  if (maxQueueSize <= 0) {
    throw new IllegalArgumentException("maxQueueSize must be greater than 0");
  }
  this.documentToAddList = new ArrayList<>();
  this.documentToDeleteList = new ArrayList<>();
  this.uploader = uploader;
  this.maxQueueSize = maxQueueSize;
}
```

6. **Update add() methods** to use `this.maxQueueSize` instead of hardcoded value

**Exact changes for DocumentUploadQueueTest.java**:
- Update test instantiation to use explicit batch size where needed
- Add tests for `getConfiguredBatchSize()` with system property
- Add tests for constructor validation (exceeds 256MB, negative values)

**Must NOT do**:
- Do NOT include changes to PushService, StreamService, or UpdateStreamService
- Do NOT include StreamDocumentUploadQueue changes
- Do NOT include any documentation files
- Do NOT change the behavioral contract (flush behavior stays the same)

**PR Title**: `feat(DocumentUploadQueue): add configurable batch size infrastructure`

**PR Description**:
```markdown
## Summary
Adds infrastructure for configurable batch sizing in DocumentUploadQueue. This is foundational work that enables services to configure their batch size.

## Changes
- Added constants: `MAX_ALLOWED_QUEUE_SIZE` (256MB), `DEFAULT_QUEUE_SIZE` (256MB), `BATCH_SIZE_PROPERTY`
- Added `getConfiguredBatchSize()` static method for system property configuration
- Added new constructor with `maxQueueSize` parameter
- Added validation to prevent exceeding 256MB API limit
- Updated existing constructor to use `getConfiguredBatchSize()` as default

## Configuration
- System property: `-Dcoveo.push.batchSize=<bytes>`
- Constructor parameter: `new DocumentUploadQueue(uploader, maxQueueSize)`
- Priority: constructor > system property > default (256MB)

## Testing
- Updated existing tests
- Added tests for system property configuration
- Added tests for validation (max size, invalid values)
```

**Dependencies**: PR1 must be merged first (for consistent null handling patterns)

**Recommended Agent Profile**:
- **Category**: `visual-engineering`
- **Skills**: [`git-master`]
  - `git-master`: Needed for precise extraction of changes from existing branch

**Implementation Approach**:
```bash
# 1. Ensure PR1 is merged, then create branch from main
git checkout main
git pull origin main
git checkout -b feat/configurable-batch-size-infrastructure

# 2. Extract DocumentUploadQueue changes from feature branch
# Option A: Use git show to get the file and manually extract relevant parts
git show feature/configurable-batch-size-256mb:src/main/java/com/coveo/pushapiclient/DocumentUploadQueue.java > /tmp/new-queue.java
# Then carefully copy only the infrastructure changes (not behavioral changes)

# Option B: Apply changes manually based on the spec above

# 3. Extract test changes
git show feature/configurable-batch-size-256mb:src/test/java/com/coveo/pushapiclient/DocumentUploadQueueTest.java > /tmp/new-test.java
# Copy relevant test updates

# 4. Verify
mvn clean compile
mvn test
mvn spotless:check

# 5. Format code
mvn spotless:apply

# 6. Commit and push
git add src/main/java/com/coveo/pushapiclient/DocumentUploadQueue.java
git add src/test/java/com/coveo/pushapiclient/DocumentUploadQueueTest.java
git commit -m "feat(DocumentUploadQueue): add configurable batch size infrastructure"
git push -u origin feat/configurable-batch-size-infrastructure

# 7. Create PR
gh pr create --title "feat(DocumentUploadQueue): add configurable batch size infrastructure" --body "..."
```

**Acceptance Criteria**:
- [ ] `mvn clean compile` succeeds
- [ ] `mvn test` passes
- [ ] `mvn spotless:check` passes
- [ ] `DocumentUploadQueue.getConfiguredBatchSize()` returns 256MB by default
- [ ] System property `coveo.push.batchSize` overrides default
- [ ] Constructor with invalid maxQueueSize throws `IllegalArgumentException`
- [ ] Existing code using `new DocumentUploadQueue(uploader)` still works

**Verification Script**:
```bash
# Test system property configuration
mvn test -Dtest=DocumentUploadQueueTest -Dcoveo.push.batchSize=134217728
```

**Commit**: YES
- Message: `feat(DocumentUploadQueue): add configurable batch size infrastructure`
- Files: `DocumentUploadQueue.java`, `DocumentUploadQueueTest.java`
- Pre-commit: `mvn test`

---

### TODO 3: Create PR3 - Services + File Container Rotation + Documentation

- [ ] 3. Create PR3: `feat(services): add configurable batch size with file container rotation`

**What to do**:

1. Create new branch `feat/configurable-batch-size-services` from `main` (after PR2 merges)
2. Extract ALL remaining changes from the feature branch
3. This is the largest PR - contains service updates, behavioral changes, tests, and documentation

**Files to include**:

| File | Change Type | Specific Changes |
|------|-------------|------------------|
| `src/main/java/com/coveo/pushapiclient/PushService.java` | MODIFY | New constructor with `maxQueueSize` |
| `src/main/java/com/coveo/pushapiclient/StreamService.java` | MODIFY | New constructors with `maxQueueSize`, update existing constructors |
| `src/main/java/com/coveo/pushapiclient/UpdateStreamService.java` | MODIFY | New constructors, remove `fileContainer` field, remove `getUploadStrategy()`, update method bodies |
| `src/main/java/com/coveo/pushapiclient/StreamDocumentUploadQueue.java` | MODIFY | New constructor, `setUpdateStreamService()`, `flushAndPush()`, override `add()` methods |
| `src/main/java/com/coveo/pushapiclient/UpdateStreamServiceInternal.java` | MODIFY | Remove file container creation from `add*()`, add `createUploadAndPush()`, modify `close()` |
| `src/test/java/com/coveo/pushapiclient/StreamDocumentUploadQueueTest.java` | MODIFY | Update tests for new behavior |
| `src/test/java/com/coveo/pushapiclient/StreamDocumentUploadQueueBatchingTest.java` | NEW | New test file (274 lines) |
| `src/test/java/com/coveo/pushapiclient/FileContainerRotationIntegrationTest.java` | NEW | New test file (225 lines) |
| `src/test/java/com/coveo/pushapiclient/UpdateStreamServiceInternalTest.java` | MODIFY | Update tests for new behavior |
| `CONFIGURATION.md` | NEW | Configuration guide (235 lines) |
| `UPGRADE_NOTES.md` | NEW | Migration guidance (171 lines) |
| `README.md` | MODIFY | Add configuration section (24 lines) |
| `samples/UpdateStreamDocuments.java` | MODIFY | Add explanatory comments (8 lines) |

**Detailed changes by file**:

#### PushService.java
- Add new constructor: `PushService(source, options, int maxQueueSize)`
- Update existing constructors to use `DEFAULT_QUEUE_SIZE`
- Pass `maxQueueSize` to `DocumentUploadQueue` constructor

#### StreamService.java
- Add new 4-param constructor: `StreamService(source, options, userAgents, int maxQueueSize)`
- Update all existing constructors to delegate to the 4-param version
- Add `userAgents != null` check before calling `setUserAgents()`
- Pass `maxQueueSize` to `DocumentUploadQueue` constructor

#### UpdateStreamService.java
- Remove `private FileContainer fileContainer;` field
- Add new 4-param constructor with `maxQueueSize`
- Update all existing constructors to delegate to 4-param version
- Add `userAgents != null` check
- Pass `null` for UploadStrategy and `maxQueueSize` to `StreamDocumentUploadQueue`
- Remove `getUploadStrategy()` method
- Update `addOrUpdate()`, `addPartialUpdate()`, `delete()` to not capture return value
- Update Javadoc to reflect new behavior

#### StreamDocumentUploadQueue.java
- Add field: `private UpdateStreamServiceInternal updateStreamService;`
- Add new constructor: `StreamDocumentUploadQueue(uploader, int maxQueueSize)`
- Add method: `setUpdateStreamService(UpdateStreamServiceInternal)`
- Add method: `flushAndPush()` - creates container, uploads, pushes
- Override `flush()` to check for null uploader
- Override `add(DocumentBuilder)` to call `flushAndPush()` on batch limit
- Override `add(DeleteDocument)` to call `flushAndPush()` on batch limit
- Override `add(PartialUpdateDocument)` to call `flushAndPush()` on batch limit

#### UpdateStreamServiceInternal.java
- Add `queue.setUpdateStreamService(this);` in constructor
- Remove `if (this.fileContainer == null)` checks from `addOrUpdate()`, `addPartialUpdate()`, `delete()`
- Add new method: `createUploadAndPush(StreamUpdate)` - handles create→upload→push workflow
- Modify `close()` to call `queue.flushAndPush()` instead of `pushFileContainer()`
- Remove `createFileContainer()` private method
- Remove `pushFileContainer()` private method

**Must NOT do**:
- Do NOT modify DocumentUploadQueue.java (already done in PR2)
- Do NOT modify PlatformClient.java (already done in PR1)
- Do NOT change any logic that wasn't in the original PR

**PR Title**: `feat(services): add configurable batch size with file container rotation`

**PR Description**:
```markdown
## Summary
Adds configurable batch size support to all push services and implements per-batch file container rotation for UpdateStreamService following Coveo's catalog stream API best practices.

## Key Changes

### Configurable Batch Size
All services now support custom batch sizes (max: 256MB):
- `PushService(source, options, maxQueueSize)`
- `StreamService(source, options, userAgents, maxQueueSize)`
- `UpdateStreamService(source, options, userAgents, maxQueueSize)`

### File Container Rotation (UpdateStreamService)
Each batch now gets its own file container:
1. Create new file container
2. Upload batch content
3. Push container immediately

This follows the [catalog stream API best practices](https://docs.coveo.com/en/p4eb0129/coveo-for-commerce/full-catalog-data-updates#update-operations).

## Breaking Changes
- Default batch size increased from 5MB to 256MB
- UpdateStreamService pushes each batch immediately instead of accumulating

## Documentation
- Added CONFIGURATION.md with complete configuration guide
- Added UPGRADE_NOTES.md with migration guidance
- Updated README.md with configuration section

## Testing
- Added StreamDocumentUploadQueueBatchingTest (new)
- Added FileContainerRotationIntegrationTest (new)
- Updated existing tests for new behavior
```

**Dependencies**: PR2 must be merged first

**Recommended Agent Profile**:
- **Category**: `visual-engineering`
- **Skills**: [`git-master`]
  - `git-master`: Complex git operations to extract remaining changes

**Implementation Approach**:
```bash
# 1. Ensure PR2 is merged, then create branch from main
git checkout main
git pull origin main
git checkout -b feat/configurable-batch-size-services

# 2. Since PR1 and PR2 changes are now in main, we can cherry-pick 
# the remaining changes more easily. However, commits are mixed.
# Best approach: copy files from feature branch and let git diff handle it

# For each source file:
git show feature/configurable-batch-size-256mb:src/main/java/com/coveo/pushapiclient/PushService.java > src/main/java/com/coveo/pushapiclient/PushService.java
git show feature/configurable-batch-size-256mb:src/main/java/com/coveo/pushapiclient/StreamService.java > src/main/java/com/coveo/pushapiclient/StreamService.java
git show feature/configurable-batch-size-256mb:src/main/java/com/coveo/pushapiclient/UpdateStreamService.java > src/main/java/com/coveo/pushapiclient/UpdateStreamService.java
git show feature/configurable-batch-size-256mb:src/main/java/com/coveo/pushapiclient/StreamDocumentUploadQueue.java > src/main/java/com/coveo/pushapiclient/StreamDocumentUploadQueue.java
git show feature/configurable-batch-size-256mb:src/main/java/com/coveo/pushapiclient/UpdateStreamServiceInternal.java > src/main/java/com/coveo/pushapiclient/UpdateStreamServiceInternal.java

# For each test file:
git show feature/configurable-batch-size-256mb:src/test/java/com/coveo/pushapiclient/StreamDocumentUploadQueueTest.java > src/test/java/com/coveo/pushapiclient/StreamDocumentUploadQueueTest.java
git show feature/configurable-batch-size-256mb:src/test/java/com/coveo/pushapiclient/StreamDocumentUploadQueueBatchingTest.java > src/test/java/com/coveo/pushapiclient/StreamDocumentUploadQueueBatchingTest.java
git show feature/configurable-batch-size-256mb:src/test/java/com/coveo/pushapiclient/FileContainerRotationIntegrationTest.java > src/test/java/com/coveo/pushapiclient/FileContainerRotationIntegrationTest.java
git show feature/configurable-batch-size-256mb:src/test/java/com/coveo/pushapiclient/UpdateStreamServiceInternalTest.java > src/test/java/com/coveo/pushapiclient/UpdateStreamServiceInternalTest.java

# For documentation:
git show feature/configurable-batch-size-256mb:CONFIGURATION.md > CONFIGURATION.md
git show feature/configurable-batch-size-256mb:UPGRADE_NOTES.md > UPGRADE_NOTES.md
git show feature/configurable-batch-size-256mb:README.md > README.md
git show feature/configurable-batch-size-256mb:samples/UpdateStreamDocuments.java > samples/UpdateStreamDocuments.java

# 3. Verify
mvn clean compile
mvn test
mvn spotless:check

# 4. Format if needed
mvn spotless:apply

# 5. Stage all changes
git add -A

# 6. Commit
git commit -m "feat(services): add configurable batch size with file container rotation

- Add configurable batch size to PushService, StreamService, UpdateStreamService
- Implement per-batch file container rotation for UpdateStreamService
- Add CONFIGURATION.md and UPGRADE_NOTES.md documentation
- Update README.md with configuration section
- Add comprehensive tests for batching and rotation behavior"

# 7. Push and create PR
git push -u origin feat/configurable-batch-size-services
gh pr create --title "feat(services): add configurable batch size with file container rotation" --body "..."
```

**Acceptance Criteria**:

**Compilation & Tests**:
- [ ] `mvn clean compile` succeeds
- [ ] `mvn test` passes all tests (including new tests)
- [ ] `mvn spotless:check` passes

**PushService**:
- [ ] New constructor `PushService(source, options, maxQueueSize)` available
- [ ] Default constructor uses 256MB batch size
- [ ] Batching works correctly with custom size

**StreamService**:
- [ ] New constructor `StreamService(source, options, userAgents, maxQueueSize)` available
- [ ] All existing constructors still work
- [ ] `userAgents` can be null without error

**UpdateStreamService**:
- [ ] New constructor `UpdateStreamService(source, options, userAgents, maxQueueSize)` available
- [ ] Each batch creates its own file container (verify via logs)
- [ ] Batches are pushed immediately when limit exceeded
- [ ] `close()` pushes remaining documents

**Documentation**:
- [ ] CONFIGURATION.md exists with complete guide
- [ ] UPGRADE_NOTES.md explains behavioral changes
- [ ] README.md has new Configuration section

**Commit**: YES
- Message: `feat(services): add configurable batch size with file container rotation`
- Files: 13 files (5 source, 4 test, 4 doc)
- Pre-commit: `mvn test`

---

## Commit Strategy

| PR | After Merge | Commit Message | Key Files |
|----|-------------|----------------|-----------|
| 1 | PR1 | `fix(PlatformClient): validate userAgents is not null` | PlatformClient.java |
| 2 | PR2 | `feat(DocumentUploadQueue): add configurable batch size infrastructure` | DocumentUploadQueue.java, DocumentUploadQueueTest.java |
| 3 | PR3 | `feat(services): add configurable batch size with file container rotation` | 13 files |

---

## Success Criteria

### Verification Commands
```bash
# After all PRs merged:
git checkout main
git pull origin main
mvn clean test              # Expected: BUILD SUCCESS
mvn spotless:check          # Expected: no violations

# Verify batch size configuration
mvn test -Dcoveo.push.batchSize=134217728  # Should use 128MB
```

### Final Checklist
- [ ] PR1 merged and passing CI
- [ ] PR2 merged and passing CI  
- [ ] PR3 merged and passing CI
- [ ] Original PR #186 closed (superseded)
- [ ] All 16 files from original PR are accounted for
- [ ] Documentation is complete and accurate
- [ ] Behavioral change (file container rotation) is working

---

## Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Merge conflicts between PRs | Low | Medium | Merge PRs in strict order; don't parallelize |
| PR2 breaks existing code | Low | High | Run full test suite; existing constructor unchanged |
| PR3 tests fail due to missing PR2 infrastructure | Medium | Medium | Ensure PR2 is fully merged before creating PR3 branch |
| Documentation out of sync | Low | Low | Include all docs in PR3 to keep them together |

---

## Post-Merge Cleanup

After PR3 is merged:
1. Close original PR #186 with comment: "Superseded by #PR1, #PR2, #PR3"
2. Delete feature branch: `git push origin --delete feature/configurable-batch-size-256mb`
3. Delete local branches created for split PRs (optional)
