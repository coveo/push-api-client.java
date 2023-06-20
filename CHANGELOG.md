# Changelog

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
