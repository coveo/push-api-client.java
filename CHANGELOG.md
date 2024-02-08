# Changelog

## [2.5.0](https://github.com/coveo/push-api-client.java/compare/v2.4.0...v2.5.0) (2024-02-08)


### Features

* add create method in `CatalogSource` and `PushSource` (LENS-874) ([#35](https://github.com/coveo/push-api-client.java/issues/35)) ([f2e93a2](https://github.com/coveo/push-api-client.java/commit/f2e93a243a9a85f88c755cd8ac1f9389a74dae52))
* add DocumentBuilder class ([#1](https://github.com/coveo/push-api-client.java/issues/1)) ([329dd8a](https://github.com/coveo/push-api-client.java/commit/329dd8a5f26533e24fa3707f8684723f4ad89da8))
* add exponential backoff to api core ([#102](https://github.com/coveo/push-api-client.java/issues/102)) ([172c936](https://github.com/coveo/push-api-client.java/commit/172c936101a56c6e9757a9e3f818ec38fbfb6cd3))
* add security identity management support ([#3](https://github.com/coveo/push-api-client.java/issues/3)) ([7b4785c](https://github.com/coveo/push-api-client.java/commit/7b4785c7e5ffa6cd078625c5ce0048f429c47265))
* add security identity suport on document push ([#5](https://github.com/coveo/push-api-client.java/issues/5)) ([4c33800](https://github.com/coveo/push-api-client.java/commit/4c338001d2ce0ed8f4fd0e5ffc148fb92dfc91c1))
* add support for batch update documents  ([#8](https://github.com/coveo/push-api-client.java/issues/8)) ([bb98f67](https://github.com/coveo/push-api-client.java/commit/bb98f67219d75fbd8fdd4912718abe64f1fd4e4e))
* add support for compressed binary data ([#10](https://github.com/coveo/push-api-client.java/issues/10)) ([40ededc](https://github.com/coveo/push-api-client.java/commit/40ededc14642c449e4bd9e9acf619db0a63b5eff))
* add support for permanentid generation ([#12](https://github.com/coveo/push-api-client.java/issues/12)) ([1b376a6](https://github.com/coveo/push-api-client.java/commit/1b376a6fde0a533bec06b96e4f5b6b24b4dfc875))
* create `PushSource` and `catalogSource` classes ([#30](https://github.com/coveo/push-api-client.java/issues/30)) ([b42c055](https://github.com/coveo/push-api-client.java/commit/b42c055cc3527e705f446a64e6fbf926c7f12eb0))
* create platform client class ([#2](https://github.com/coveo/push-api-client.java/issues/2)) ([191acb1](https://github.com/coveo/push-api-client.java/commit/191acb1853501ccf479176ba68393d77b3bf856b))
* enable push on catalog source ([#82](https://github.com/coveo/push-api-client.java/issues/82)) ([aee3434](https://github.com/coveo/push-api-client.java/commit/aee34340e53c179963e8adfe25168071f6f435b4))
* expose publicly PushSource,CatalogSource,StreamService ([#46](https://github.com/coveo/push-api-client.java/issues/46)) ([dd00a0e](https://github.com/coveo/push-api-client.java/commit/dd00a0ef8f2df82ceb6e09eeb0f69671d9c99d5d))
* final all the things ([#6](https://github.com/coveo/push-api-client.java/issues/6)) ([cc16426](https://github.com/coveo/push-api-client.java/commit/cc1642603aefc1ad751019f22ed4e54dddcbd596))
* implement `DocumentUploadQueue` class ([#33](https://github.com/coveo/push-api-client.java/issues/33)) ([ed0f754](https://github.com/coveo/push-api-client.java/commit/ed0f754b5cf16527a92070bab8a58cdfa8287dfe))
* implement `PushService` class ([#34](https://github.com/coveo/push-api-client.java/issues/34)) ([28a7a22](https://github.com/coveo/push-api-client.java/commit/28a7a220e0fe48df52d542f585e553014ef4e271))
* implement `StreamService` class ([#32](https://github.com/coveo/push-api-client.java/issues/32)) ([75d605f](https://github.com/coveo/push-api-client.java/commit/75d605f2d88d02e9ff3e045f1cc56356c1a2bc46))
* initial commit ([10111cb](https://github.com/coveo/push-api-client.java/commit/10111cb7c8e7d9229f0688fe7daacc2b3c9c2aea))
* **LENS-1390:** add support for incremental catalog updates in Java SDK ([#120](https://github.com/coveo/push-api-client.java/issues/120)) ([7d172ad](https://github.com/coveo/push-api-client.java/commit/7d172ad02541f939512e17f1b6d2b65afe47f42a))
* log HTTP calls ([#93](https://github.com/coveo/push-api-client.java/issues/93)) ([0cd3c60](https://github.com/coveo/push-api-client.java/commit/0cd3c60409d06a6b7438e233ec2c869530c4ecf5))
* **permanentid:** replace sha256 by md5(30)+sha1(30) ([#17](https://github.com/coveo/push-api-client.java/issues/17)) ([d9484ee](https://github.com/coveo/push-api-client.java/commit/d9484ee590a8c837f1657494d58306fef1263efa))
* support adding metadata on document ([#7](https://github.com/coveo/push-api-client.java/issues/7)) ([7fc0fa5](https://github.com/coveo/push-api-client.java/commit/7fc0fa5981283e75bfaa870967c43f8ab85c1981))
* support canada region ([#113](https://github.com/coveo/push-api-client.java/issues/113)) ([57140be](https://github.com/coveo/push-api-client.java/commit/57140be06d76042cf66d110e78469b48158728a5))
* Support different platform regions ([#22](https://github.com/coveo/push-api-client.java/issues/22)) ([9ffe72b](https://github.com/coveo/push-api-client.java/commit/9ffe72bd17c640594898a037dc248066b99a6c9f))
* support pushing single document with no permissions ([#4](https://github.com/coveo/push-api-client.java/issues/4)) ([92d9dc0](https://github.com/coveo/push-api-client.java/commit/92d9dc02fecd487b42026e095eb9ff8eae5d0bdd))
* support single document deletion ([#9](https://github.com/coveo/push-api-client.java/issues/9)) ([3b5dcc4](https://github.com/coveo/push-api-client.java/commit/3b5dcc4edec5cdf696135e756cd2a960b91d5863))
* validate file extension ([635d9c0](https://github.com/coveo/push-api-client.java/commit/635d9c0f81519e936c49a3b3a16feb15f1da2f7b))


### Bug Fixes

* bug related to the type expected of delete documents ([37dd38c](https://github.com/coveo/push-api-client.java/commit/37dd38c12797f4dea4d1a2d088e264942f25c23f))
* **deps:** update all dependencies j:cdx-227 ([#85](https://github.com/coveo/push-api-client.java/issues/85)) ([fd7372e](https://github.com/coveo/push-api-client.java/commit/fd7372e692eba59446c3c122c8e2f0c86c6fe319))
* **deps:** update dependency com.google.code.gson:gson to v2.8.9 [security] ([#57](https://github.com/coveo/push-api-client.java/issues/57)) ([ffe1652](https://github.com/coveo/push-api-client.java/commit/ffe1652d60601f028eac0e43f2b7a51c483bd979))
* in StreamService streamId was never set , with this PR , it is returned from StreamServiceInternal ([#42](https://github.com/coveo/push-api-client.java/issues/42)) ([fbf9ccb](https://github.com/coveo/push-api-client.java/commit/fbf9ccbb23a132a6f13fa35c0eb456ec6636f79b))
* provide JSON value ([#36](https://github.com/coveo/push-api-client.java/issues/36)) ([c3025bb](https://github.com/coveo/push-api-client.java/commit/c3025bbe96933902bb31b181734a60bcea1420f5))


### Documentation

* add install instructions ([#92](https://github.com/coveo/push-api-client.java/issues/92)) ([8840df4](https://github.com/coveo/push-api-client.java/commit/8840df44dc6af6c83593c86066d609b78d5e3805))
* create sample code  to stream document ([#43](https://github.com/coveo/push-api-client.java/issues/43)) ([8cec4a0](https://github.com/coveo/push-api-client.java/commit/8cec4a095b090ae13a7ad66dd1fa79bd45770b8f))
* create Sample Code classes to create push and Catalog Source in Coveo and instance Java classes in different ways  ([#39](https://github.com/coveo/push-api-client.java/issues/39)) ([309ecde](https://github.com/coveo/push-api-client.java/commit/309ecdeec270d85e001ec4a1414cc4733f5032c3))
* improve retry configuration documentation ([#104](https://github.com/coveo/push-api-client.java/issues/104)) ([5c84117](https://github.com/coveo/push-api-client.java/commit/5c8411793ad5eae4f94af6ee55451c1749cffaca))
* specify in readme that access token needs to have read:packages permission ([c99d06d](https://github.com/coveo/push-api-client.java/commit/c99d06d9e7d626c74a165df05eaf3aaeed703795))
* updates the readme: LENS-979 ([#96](https://github.com/coveo/push-api-client.java/issues/96)) ([c6259f8](https://github.com/coveo/push-api-client.java/commit/c6259f84dc1d61bb6ee290c95f81ecb6d949805a))
* use appropriate javadoc url syntax ([#77](https://github.com/coveo/push-api-client.java/issues/77)) ([c5f1d96](https://github.com/coveo/push-api-client.java/commit/c5f1d966e7bd757459a1a1de14c25ec7650de086))


### Miscellaneous Chores

* release 2.3.0 ([ad91e6e](https://github.com/coveo/push-api-client.java/commit/ad91e6eb12d329d50549c3cc5f219819d8f0d66b))
* release 2.3.1 ([cde6667](https://github.com/coveo/push-api-client.java/commit/cde66677d58a87782a2c0125ebf98cd24a7b19fe))
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
