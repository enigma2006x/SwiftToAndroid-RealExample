# TapCounter

This repository demonstrates, through a practical example, how to implement and use a shared native library with `TapCounterLib` for both Android and iOS.

The project is organized into three main folders:

- `TapCounterLib/`: Shared library implemented in Swift. It contains the `TapCounter` domain logic, Swift Package Manager configuration, tests, and the `swift-java.config` file used to generate JNI bindings for Android-oriented integration.
- `TapCounter_Android/`: Android sample app built with Kotlin and Jetpack Compose. This folder contains the Android application module, Gradle configuration, resources, and the UI shell that will consume the shared library on Android.
- `TapCounter_iOS/`: iOS sample app built with SwiftUI. This folder contains the Xcode project and the iOS interface used to demonstrate the same tap-counter experience on Apple platforms.

## What This Repository Shows

The goal of this repository is to keep the tap counter logic in one shared library and expose it to platform-specific apps:

- On Android, `TapCounterLib` is prepared for JNI-based interop through `swift-java`, which can be used to generate the Java/Kotlin wrappers and native glue needed to work with a `.so` library.
- On iOS, the same shared logic is represented alongside a native SwiftUI application structure.

## Folder Overview

### `TapCounterLib`

This folder contains the reusable counter logic:

- `Package.swift`: Swift package definition for the dynamic library product `TapCounterLib`.
- `Sources/TapCounterLib/TapCounterLib.swift`: Core tap counter implementation with `tap()`, `reset()`, `currentCount`, and `label()`.
- `Sources/TapCounterLib/swift-java.config`: Configuration for generating JNI-compatible bindings under the Java package `com.example.tapcounter`.
- `Tests/TapCounterLibTests/`: Unit test target for the shared library.

### `TapCounter_Android`

This folder contains the Android demo application:

- `app/src/main/java/com/example/tapcounterandroid/MainActivity.kt`: Jetpack Compose UI for the tap counter screen.
- `app/build.gradle.kts`: Android application configuration, SDK levels, and Compose setup.
- `app/src/main/res/`: Android resources such as strings, themes, colors, and launcher assets.
- `gradle/`, `settings.gradle.kts`, `build.gradle.kts`: Project-level Gradle configuration.

### `TapCounter_iOS`

This folder contains the iOS demo application:

- `TapCounter/TapCounterApp.swift`: App entry point.
- `TapCounter/ContentView.swift`: SwiftUI-based tap counter screen.
- `TapCounter.xcodeproj/`: Xcode project configuration for building and running the app.

## Purpose

This repository is intended as a learning and reference project for teams who want to:

- centralize business logic in a reusable native library,
- expose that logic to Android through generated JNI bindings and shared native artifacts,
- keep a corresponding iOS app alongside the same example,
- and understand how a small cross-platform integration can be structured in practice.
