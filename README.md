# SwiftToAndroid-RealExample

![Swift 6.3 to Android Part 2](<Swift 6.3 → Android .so Share One Codebase Across iOS & Android (Live Demo) — Part 2.png>)

This repository shows one shared Swift business-logic implementation used by both iOS and Android.

The shared tap-counter logic lives in `TapCounterSwiftLib`, the iOS app consumes that package directly, and the Android side uses `TapCounterAndroidLib` as a JNI/export bridge that generates Java bindings and `.so` artifacts through `swift-java`.

## Video Series

This repository accompanies a two-part walkthrough showing how to build and then use a shared Swift 6.3 codebase across iOS and Android:

- Part 1:
  Build the Android `.so` from Swift 6.3 with Swift Package Manager.
  [Watch Part 1](https://www.youtube.com/watch?v=S1WsA1XU0vs)
- Part 2:
  Wire the generated `.so` into the Android app, keep iOS consuming the shared Swift package directly, and explain the architecture and integration issues.
  [Watch Part 2](https://www.youtube.com/watch?v=sExcX53AuWw)

If you are starting from scratch, watch Part 1 first. Part 2 assumes the native Android artifact generation flow is already in place.

## Repository Layout

- `TapCounterSwiftLib/`
  The single source of truth for the shared `TapCounter` logic used by both apps.
- `TapCounterAndroidLib/`
  Android interop package. It depends on `TapCounterSwiftLib` and uses `swift-java` plus `JExtractSwiftPlugin` to generate Java bindings and Android native libraries.
- `TapCounter_Android/`
  Kotlin/Jetpack Compose Android app that loads the generated `.so` files and calls the generated Java wrapper classes.
- `TapCounter_iOS/`
  SwiftUI iOS app that consumes the shared Swift package directly through Swift Package Manager.

## Architecture

### Shared Swift Logic

`TapCounterSwiftLib` contains the real implementation of the tap counter:

- `TapCounter`
- `tap()`
- `reset()`
- `currentCount`
- `label()`

This package is intended to stay platform-safe and reusable.

### Android Bridge

`TapCounterAndroidLib` exists only to expose the shared Swift logic to Android:

- it depends on `TapCounterSwiftLib`
- it depends on `swift-java`
- it uses `JExtractSwiftPlugin`
- it exports a thin bridge type for JNI generation

The bridge target should not own business logic. It only adapts the shared Swift package for Android/native interop.

This separation is the key architectural choice shown in Part 2:

- `TapCounterSwiftLib` stays clean and Swift-first for Apple platforms
- `TapCounterAndroidLib` stays Android-specific and owns the interop/export concerns
- the business logic remains shared, even though the delivery mechanism differs by platform

### Android App Integration

The Android app integrates with the generated native layer in two parts:

- native libraries are staged automatically during build by Gradle
- generated Java bindings are referenced directly from `TapCounterAndroidLib/.build/...`

The app currently uses the generated `TapCounterBridge` wrapper from:

- `com.tonytrejo.TapCounterAndroidLib.TapCounterBridge`

and loads:

- `SwiftJava`
- `TapCounterAndroidLib`

from `MainActivity`.

## How The Series Maps To This Repo

### Part 1: Generate the Android Native Layer

Part 1 focuses on producing the Android native output from Swift 6.3:

1. Keep the shared business logic in `TapCounterSwiftLib`.
2. Use `TapCounterAndroidLib` as the Android-facing bridge package.
3. Generate the Android `.so` artifact and Java bindings with `swift-java`.
4. Treat the generated output under `.build/...` as the source for Android integration.

### Part 2: Use The Same Swift Logic In Both Apps

Part 2 shows the full end-to-end usage:

1. The iOS app imports `TapCounterSwiftLib` as a local Swift package.
2. The Android app uses Jetpack Compose for UI, but calls into Swift through `TapCounterBridge`.
3. Gradle stages the generated `.so` plus its native dependencies into the Android app.
4. Both apps end up running the same counter behavior from the same Swift implementation.

## Important Android Build Details

The Android app no longer relies on manually copied runtime `.so` files in `app/src/main/jniLibs`.

Instead, [app/build.gradle.kts](/Users/zoro/Documents/TapCounter/TapCounter_Android/app/build.gradle.kts) defines a Gradle `Sync` task named `syncSwiftNativeLibs` that gathers:

- `libTapCounterAndroidLib.so`
- `libSwiftJava.so`
- Swift runtime `.so` files from the installed Swift Android SDK
- `libc++_shared.so` from the Android NDK bundled with that SDK

These are staged into:

- `TapCounter_Android/app/build/generated/jniLibs`

and the app’s `jniLibs` source set points at that generated directory.

This means:

- no manual copying is required for runtime `.so` files
- the Android APK still packages all required native dependencies
- the build stays reproducible as long as the Swift Android SDK is installed locally

## Generated Java Bindings

`swift-java` generates Java wrapper classes for the Android bridge package under:

- `TapCounterAndroidLib/.build/plugins/outputs/.../src/generated/java`

The Android app references that generated source path directly, so when the Swift bridge type changes and bindings are regenerated, Android Studio can pick up the new generated classes without relying on stale copied wrapper files.

## End-To-End Flow

1. Update shared counter behavior in `TapCounterSwiftLib`.
2. Keep `TapCounterAndroidLib` as a bridge-only package for Android export.
3. Regenerate Android bindings and `.so` artifacts from `TapCounterAndroidLib`.
4. Run the iOS app, which imports the shared Swift package directly.
5. Build the Android app, which stages native libraries with Gradle, compiles against generated Java bindings, and uses the Swift-backed tap counter from Compose.

## Notes

- `TapCounterSwiftLib` is the only place where counter business logic should live.
- `TapCounterAndroidLib` is bridge/export glue, not a second logic implementation.
- `TapCounter_iOS` and `TapCounter_Android` are intentionally thin host apps around the same shared logic.
- The Android setup depends on a local Swift Android SDK installation. The Gradle script currently defaults to:
  `~/Library/org.swift.swiftpm/swift-sdks/swift-6.3-RELEASE_android.artifactbundle/swift-android`
- If needed, that path can be overridden with the Gradle property:
  `swiftAndroidSdkRoot`

## Troubleshooting

### Android Startup Crash: `liblibSwiftJava.so` Not Found

Error:

- `java.lang.UnsatisfiedLinkError: dlopen failed: library "liblibSwiftJava.so" not found`

Cause:

- `System.loadLibrary(...)` was called with the `lib` prefix included.

Wrong:

```kotlin
System.loadLibrary("libSwiftJava")
System.loadLibrary("libTapCounterAndroidLib")
```

Correct:

```kotlin
System.loadLibrary("SwiftJava")
System.loadLibrary("TapCounterAndroidLib")
```

Android automatically adds the `lib` prefix and `.so` suffix.

### Android Startup Crash: Missing Swift Runtime Libraries

Errors seen:

- `libswiftSwiftOnoneSupport.so not found`
- `libswiftCore.so not found`
- `libswift_Concurrency.so not found`
- similar `UnsatisfiedLinkError` messages for Swift runtime `.so` files

Cause:

- `libSwiftJava.so` and `libTapCounterAndroidLib.so` depend on additional Swift runtime libraries that must be packaged into the APK.

Fix:

- use the Gradle `syncSwiftNativeLibs` task in [app/build.gradle.kts](/Users/zoro/Documents/TapCounter/TapCounter_Android/app/build.gradle.kts)
- stage Swift runtime `.so` files from the installed Swift Android SDK into `build/generated/jniLibs`

### Android Startup Crash: `libc++_shared.so` Not Found

Error:

- `java.lang.UnsatisfiedLinkError: dlopen failed: library "libc++_shared.so" not found`

Cause:

- the Swift/NDK native libraries also depend on the Android C++ shared runtime.

Fix:

- package `libc++_shared.so` from the Android NDK bundled in the Swift Android SDK
- this is handled automatically by the Gradle `syncSwiftNativeLibs` task

### Android Studio Shows Stale Generated Classes

Symptoms:

- Android Studio still shows `TapCounter` after the Swift bridge was renamed to `TapCounterBridge`
- imports stay red even though generated bindings were rebuilt

Cause:

- the app was pointing at copied generated Java files under `app/src/main/java/...`
- regenerating bindings in `TapCounterAndroidLib/.build/...` does not automatically update copied files

Fix:

- stop copying generated Java wrapper files
- point the Android app directly to:
  `TapCounterAndroidLib/.build/plugins/outputs/.../src/generated/java`
- this is now configured in [app/build.gradle.kts](/Users/zoro/Documents/TapCounter/TapCounter_Android/app/build.gradle.kts)

### Android Java Compile Error: `package jdk.jfr does not exist`

Errors seen:

- `package jdk.jfr does not exist`
- `cannot find symbol class Label`
- `cannot find symbol class Description`

Cause:

- some `SwiftKitCore` annotation classes from `swift-java` imported `jdk.jfr.*`, which is not available on Android

Fix:

- keep Android-safe local versions of:
  - `org.swift.swiftkit.core.annotations.ThreadSafe`
  - `org.swift.swiftkit.core.annotations.Unsigned`
- compile the app against the local Android-compatible versions

### Xcode / SwiftPM: `No such module 'TapCounterSwiftLib'`

Symptoms:

- Xcode shows `No such module 'TapCounterSwiftLib'`
- the package dependency appears with a `?`

Cause:

- Xcode package resolution can get stale or confused even when SwiftPM itself resolves the local dependency correctly

Fixes that help:

1. Reset package caches in Xcode
2. Resolve package versions again
3. Reopen the package root instead of only opening an individual Swift file
4. If needed, delete:
   - `TapCounterAndroidLib/.build`
   - `TapCounterAndroidLib/.swiftpm`
5. Use the canonical package identity in `Package.swift`

### SwiftPM Error: `target 'TapCounterAndroidLib' referenced in product 'TapCounterAndroidLib' is empty`

Error:

- `target 'TapCounterAndroidLib' referenced in product 'TapCounterAndroidLib' is empty`

Cause:

- the target only had `swift-java.config` and no Swift source files

Fix:

- add a real Swift source file to `TapCounterAndroidLib`
- keep that file bridge-only
- delegate all real logic to `TapCounterSwiftLib`

### Architecture Clarification

Goal:

- one logic implementation for both iOS and Android

How this repository handles it:

- `TapCounterSwiftLib` owns the only real `TapCounter` logic
- `TapCounterAndroidLib` contains only a bridge/export surface for JNI generation
- Android uses the generated Java wrapper around that bridge

This means there is one business-logic implementation, even though Android still needs a thin exported bridge type for tooling reasons.
