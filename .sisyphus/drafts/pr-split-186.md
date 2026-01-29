# Draft: PR #186 Split Strategy

## User's Request
Split PR #186 (feat: increase batch size to 256MB and add configurable batching) into 2-4 smaller, independently mergeable PRs.

## PR Analysis Summary

**Total Changes**: 1368 additions, 136 deletions, 16 files
**Branch**: `feature/configurable-batch-size-256mb` (6 commits)

### Change Categories Identified

1. **Core Infrastructure** (foundational - everything depends on this)
   - `DocumentUploadQueue.java` - Constants, `getConfiguredBatchSize()`, new constructor with maxQueueSize
   - `PlatformClient.java` - Null check for `setUserAgents()` (minor fix)

2. **Service Layer Updates** (depends on #1)
   - `PushService.java` - New constructor with maxQueueSize
   - `StreamService.java` - New constructor with maxQueueSize
   - `UpdateStreamService.java` - New constructors with maxQueueSize

3. **Behavioral Change: File Container Rotation** (depends on #1, #2)
   - `StreamDocumentUploadQueue.java` - `setUpdateStreamService()`, `flushAndPush()`, overridden `add()` methods
   - `UpdateStreamServiceInternal.java` - `createUploadAndPush()`, modified `close()`

4. **Documentation**
   - `CONFIGURATION.md` (new, 235 lines)
   - `UPGRADE_NOTES.md` (new, 171 lines)
   - `README.md` (24 lines added)
   - `samples/UpdateStreamDocuments.java` (8 lines)

5. **Tests**
   - `DocumentUploadQueueTest.java` - Updated for explicit batch size
   - `StreamDocumentUploadQueueTest.java` - Updated tests
   - `StreamDocumentUploadQueueBatchingTest.java` (new, 274 lines)
   - `FileContainerRotationIntegrationTest.java` (new, 225 lines)
   - `UpdateStreamServiceInternalTest.java` - Updated tests

## Key Dependency Analysis

```
DocumentUploadQueue (constants + getConfiguredBatchSize + constructor)
         │
         ├──► PushService (uses DEFAULT_QUEUE_SIZE, new constructor)
         ├──► StreamService (uses getConfiguredBatchSize(), new constructor)
         └──► UpdateStreamService (uses getConfiguredBatchSize(), new constructor)
                    │
                    └──► StreamDocumentUploadQueue (extends DocumentUploadQueue)
                              │
                              └──► UpdateStreamServiceInternal (uses queue.flushAndPush())
```

## Technical Constraints

1. **`DocumentUploadQueue` changes are foundational** - Services reference:
   - `DEFAULT_QUEUE_SIZE` (constant)
   - `getConfiguredBatchSize()` (static method)
   - Constructor with `maxQueueSize` parameter

2. **`StreamDocumentUploadQueue.flushAndPush()` is tightly coupled to `UpdateStreamServiceInternal.createUploadAndPush()`**
   - These MUST be in the same PR or the code won't compile

3. **PlatformClient null check** is a simple fix that could go anywhere

## Open Questions

1. Should documentation be bundled with features or kept separate?
2. Should `PlatformClient` null check fix be in PR1 or separate?
3. Is there value in splitting service constructors from the core infrastructure?

## Proposed Split Strategy (Preliminary)

### Option A: 3 PRs (Balanced)
- PR1: Core Infrastructure + Service Constructors + Tests
- PR2: File Container Rotation Logic + Tests  
- PR3: Documentation

### Option B: 2 PRs (Simpler)
- PR1: All Code + Tests
- PR2: Documentation

### Option C: 4 PRs (Maximum Separation)
- PR1: Core Infrastructure (DocumentUploadQueue + PlatformClient fix)
- PR2: Service Constructors (PushService, StreamService, UpdateStreamService)
- PR3: File Container Rotation (StreamDocumentUploadQueue, UpdateStreamServiceInternal)
- PR4: Documentation

## Research Findings

- The PR follows Coveo's catalog stream API best practices
- Default batch size increased from 5MB to 256MB (51x increase)
- Breaking behavioral change: each batch now gets its own file container and is pushed immediately
- Project uses Maven, JUnit 4 + Mockito for testing
- Spotless plugin enforces Google Java Format

## Additional Context from Explore Agent

### Class Relationships Confirmed
- `DocumentUploadQueue` is the base queue class (package-private)
- `StreamDocumentUploadQueue` extends `DocumentUploadQueue`, adds:
  - `PartialUpdateDocument` support
  - `flushAndPush()` method that calls `UpdateStreamServiceInternal.createUploadAndPush()`
  - `setUpdateStreamService()` to link back to internal service
- `UpdateStreamServiceInternal` orchestrates the 3-step workflow: create container → upload → push
- High-level services (`PushService`, `StreamService`, `UpdateStreamService`) are facades that wire queues + PlatformClient

### Test Infrastructure
- JUnit 4 with `@Test`, `@Before`, `@After`
- Heavy Mockito usage: `@Mock`, `@InjectMocks`, `verify`, `when`, `ArgumentCaptor`
- Existing batching tests validate system property configuration
- FileContainerRotationIntegrationTest uses mocks for end-to-end container rotation

### Commit History (6 commits)
1. `45282f1` - feat: add configurable batch size with 256MB default and runtime configuration
2. `8280d47` - Update docs and add UpdateStreamService constructor
3. `68e46e1` - tests: test new file container rotation logic
4. `6d05dfe` - fix issues
5. `66b789e` - fix null user agent
6. `682bb0d` - format

## Questions for User (Decision Points)
1. Documentation bundling strategy?
2. PlatformClient null check - separate or bundle?
3. Preferred PR count: 2, 3, or 4?
