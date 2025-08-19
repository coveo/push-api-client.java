# Changelog

## [2.6.2](https://github.com/coveo/push-api-client.java/compare/v2.6.1...v2.6.2) (2025-08-19)


### Bug Fixes

* **deps:** update all dependencies j:cdx-227 ([#171](https://github.com/coveo/push-api-client.java/issues/171)) ([1fd3770](https://github.com/coveo/push-api-client.java/commit/1fd3770deef67b6fbd3586929838ea2de6cc45ab))


### Documentation

* **readme:** add info to code snippets ([#173](https://github.com/coveo/push-api-client.java/issues/173)) ([5c7afd4](https://github.com/coveo/push-api-client.java/commit/5c7afd45c8c8fd86b929798fe70565e2602484ab))

## [2.6.1](https://github.com/coveo/push-api-client.java/compare/v2.6.0...v2.6.1) (2024-04-30)


### Bug Fixes

* **LENS-1799:** logging improvements ([#131](https://github.com/coveo/push-api-client.java/issues/131)) ([294d61b](https://github.com/coveo/push-api-client.java/commit/294d61bdd4e2d9debb50ea20383e77251544fb49))

## [2.6.0](https://github.com/coveo/push-api-client.java/compare/v2.5.0...v2.6.0) (2024-04-16)


### Features

* **LENS-1466:** add support for partial updates in the UpdateStreamService ([#127](https://github.com/coveo/push-api-client.java/issues/127)) ([7725e54](https://github.com/coveo/push-api-client.java/commit/7725e543985a2bef8b2ea5ff4bf135e3f69eee32))
* **LENS-1642:** support for SAP specific user-agent header ([#128](https://github.com/coveo/push-api-client.java/issues/128)) ([279a26b](https://github.com/coveo/push-api-client.java/commit/279a26bead907fb462ff194b96b84bf5c4f92cf3))


### Documentation

* update manual release process ([#126](https://github.com/coveo/push-api-client.java/issues/126)) ([a6dd522](https://github.com/coveo/push-api-client.java/commit/a6dd522acb62a168adde512bfe50f8199c7fb20c))

## [2.5.0](https://github.com/coveo/push-api-client.java/compare/v2.4.0...v2.5.0) (2024-02-08)


### Features

* **LENS-1390:** add support for incremental catalog updates in Java SDK ([#120](https://github.com/coveo/push-api-client.java/issues/120)) ([7d172ad](https://github.com/coveo/push-api-client.java/commit/7d172ad02541f939512e17f1b6d2b65afe47f42a))

### Miscellaneous Chores

* release 2.5.0 ([#124](https://github.com/coveo/push-api-client.java/issues/124)) ([89ad27d](https://github.com/coveo/push-api-client.java/commit/89ad27d6a8a94ce7817115b965e29459cbd8e647))

## [2.4.0](https://github.com/coveo/push-api-client.java/compare/v2.3.2...v2.4.0) (2023-11-22)


### Features

* support canada region ([#113](https://github.com/coveo/push-api-client.java/issues/113)) ([57140be](https://github.com/coveo/push-api-client.java/commit/57140be06d76042cf66d110e78469b48158728a5))

## [2.3.2](https://github.com/coveo/push-api-client.java/compare/v2.3.1...v2.3.2) (2023-10-26)


### Documentation

* specify in readme that access token needs to have read:packages permission ([c99d06d](https://github.com/coveo/push-api-client.java/commit/c99d06d9e7d626c74a165df05eaf3aaeed703795))

## [2.3.1](https://github.com/coveo/push-api-client.java/compare/v2.3.0...v2.3.1) (2023-09-20)


### Features

* add exponential backoff to api core ([#102](https://github.com/coveo/push-api-client.java/issues/102)) ([172c936](https://github.com/coveo/push-api-client.java/commit/172c936101a56c6e9757a9e3f818ec38fbfb6cd3))
* enable push on catalog source ([#82](https://github.com/coveo/push-api-client.java/issues/82)) ([aee3434](https://github.com/coveo/push-api-client.java/commit/aee34340e53c179963e8adfe25168071f6f435b4))
* log HTTP calls ([#93](https://github.com/coveo/push-api-client.java/issues/93)) ([0cd3c60](https://github.com/coveo/push-api-client.java/commit/0cd3c60409d06a6b7438e233ec2c869530c4ecf5))


### Bug Fixes

* **deps:** update all dependencies j:cdx-227 ([#85](https://github.com/coveo/push-api-client.java/issues/85)) ([fd7372e](https://github.com/coveo/push-api-client.java/commit/fd7372e692eba59446c3c122c8e2f0c86c6fe319))


### Documentation

* add install instructions ([#92](https://github.com/coveo/push-api-client.java/issues/92)) ([8840df4](https://github.com/coveo/push-api-client.java/commit/8840df44dc6af6c83593c86066d609b78d5e3805))
* improve retry configuration documentation ([#104](https://github.com/coveo/push-api-client.java/issues/104)) ([5c84117](https://github.com/coveo/push-api-client.java/commit/5c8411793ad5eae4f94af6ee55451c1749cffaca))
* updates the readme: LENS-979 ([#96](https://github.com/coveo/push-api-client.java/issues/96)) ([c6259f8](https://github.com/coveo/push-api-client.java/commit/c6259f84dc1d61bb6ee290c95f81ecb6d949805a))


### Miscellaneous Chores

* release 2.3.1 ([cde6667](https://github.com/coveo/push-api-client.java/commit/cde66677d58a87782a2c0125ebf98cd24a7b19fe))

## [2.3.0](https://github.com/coveo/push-api-client.java/compare/v2.2.0...v2.3.0) (2023-06-20)


### Features

* add create method in `CatalogSource` and `PushSource` (LENS-874) ([#35](https://github.com/coveo/push-api-client.java/issues/35)) ([f2e93a2](https://github.com/coveo/push-api-client.java/commit/f2e93a243a9a85f88c755cd8ac1f9389a74dae52))
* create `PushSource` and `catalogSource` classes ([#30](https://github.com/coveo/push-api-client.java/issues/30)) ([b42c055](https://github.com/coveo/push-api-client.java/commit/b42c055cc3527e705f446a64e6fbf926c7f12eb0))
* expose publicly PushSource,CatalogSource,StreamService ([#46](https://github.com/coveo/push-api-client.java/issues/46)) ([dd00a0e](https://github.com/coveo/push-api-client.java/commit/dd00a0ef8f2df82ceb6e09eeb0f69671d9c99d5d))
* implement `DocumentUploadQueue` class ([#33](https://github.com/coveo/push-api-client.java/issues/33)) ([ed0f754](https://github.com/coveo/push-api-client.java/commit/ed0f754b5cf16527a92070bab8a58cdfa8287dfe))
* implement `PushService` class ([#34](https://github.com/coveo/push-api-client.java/issues/34)) ([28a7a22](https://github.com/coveo/push-api-client.java/commit/28a7a220e0fe48df52d542f585e553014ef4e271))
* implement `StreamService` class ([#32](https://github.com/coveo/push-api-client.java/issues/32)) ([75d605f](https://github.com/coveo/push-api-client.java/commit/75d605f2d88d02e9ff3e045f1cc56356c1a2bc46))


### Bug Fixes

* bug related to the type expected of delete documents ([37dd38c](https://github.com/coveo/push-api-client.java/commit/37dd38c12797f4dea4d1a2d088e264942f25c23f))
* **deps:** update dependency com.google.code.gson:gson to v2.8.9 [security] ([#57](https://github.com/coveo/push-api-client.java/issues/57)) ([ffe1652](https://github.com/coveo/push-api-client.java/commit/ffe1652d60601f028eac0e43f2b7a51c483bd979))
* in StreamService streamId was never set , with this PR , it is returned from StreamServiceInternal ([#42](https://github.com/coveo/push-api-client.java/issues/42)) ([fbf9ccb](https://github.com/coveo/push-api-client.java/commit/fbf9ccbb23a132a6f13fa35c0eb456ec6636f79b))
* provide JSON value ([#36](https://github.com/coveo/push-api-client.java/issues/36)) ([c3025bb](https://github.com/coveo/push-api-client.java/commit/c3025bbe96933902bb31b181734a60bcea1420f5))


### Documentation

* create sample code  to stream document ([#43](https://github.com/coveo/push-api-client.java/issues/43)) ([8cec4a0](https://github.com/coveo/push-api-client.java/commit/8cec4a095b090ae13a7ad66dd1fa79bd45770b8f))
* create Sample Code classes to create push and Catalog Source in Coveo and instance Java classes in different ways  ([#39](https://github.com/coveo/push-api-client.java/issues/39)) ([309ecde](https://github.com/coveo/push-api-client.java/commit/309ecdeec270d85e001ec4a1414cc4733f5032c3))
* use appropriate javadoc url syntax ([#77](https://github.com/coveo/push-api-client.java/issues/77)) ([c5f1d96](https://github.com/coveo/push-api-client.java/commit/c5f1d966e7bd757459a1a1de14c25ec7650de086))


### Miscellaneous Chores

* release 2.3.0 ([ad91e6e](https://github.com/coveo/push-api-client.java/commit/ad91e6eb12d329d50549c3cc5f219819d8f0d66b))
