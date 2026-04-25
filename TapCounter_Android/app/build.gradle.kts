plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

val swiftAndroidSdkRoot = providers.gradleProperty("swiftAndroidSdkRoot")
    .map(::file)
    .orElse(
        file(
            "${System.getProperty("user.home")}/Library/org.swift.swiftpm/swift-sdks/" +
                "swift-6.3-RELEASE_android.artifactbundle/swift-android"
        )
    )

val tapCounterAndroidLibRoot = rootDir.resolve("../TapCounterAndroidLib")

val syncSwiftNativeLibs by tasks.registering(Sync::class) {
    into(layout.buildDirectory.dir("generated/jniLibs"))

    from(tapCounterAndroidLibRoot.resolve(".build/aarch64-unknown-linux-android28/debug")) {
        include("libTapCounterAndroidLib.so", "libSwiftJava.so")
        into("arm64-v8a")
    }

    from(tapCounterAndroidLibRoot.resolve(".build/x86_64-unknown-linux-android28/debug")) {
        include("libTapCounterAndroidLib.so", "libSwiftJava.so")
        into("x86_64")
    }

    from(swiftAndroidSdkRoot.map { it.resolve("swift-resources/usr/lib/swift-aarch64/android") }) {
        include("*.so")
        into("arm64-v8a")
    }

    from(swiftAndroidSdkRoot.map { it.resolve("swift-resources/usr/lib/swift-x86_64/android") }) {
        include("*.so")
        into("x86_64")
    }

    from(swiftAndroidSdkRoot.map { it.resolve("android-ndk-r27d/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/lib/aarch64-linux-android") }) {
        include("libc++_shared.so")
        into("arm64-v8a")
    }

    from(swiftAndroidSdkRoot.map { it.resolve("android-ndk-r27d/toolchains/llvm/prebuilt/darwin-x86_64/sysroot/usr/lib/x86_64-linux-android") }) {
        include("libc++_shared.so")
        into("x86_64")
    }
}

android {
    namespace = "com.example.tapcounterandroid"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.tapcounterandroid"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    sourceSets {
        getByName("main") {
            jniLibs.setSrcDirs(listOf(layout.buildDirectory.dir("generated/jniLibs").get().asFile))
            java.srcDir(
                rootDir.resolve(
                    "../TapCounterAndroidLib/.build/plugins/outputs/tapcounterandroidlib/TapCounterAndroidLib/destination/JExtractSwiftPlugin/src/generated/java"
                )
            )
        }
    }
}

tasks.named("preBuild") {
    dependsOn(syncSwiftNativeLibs)
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
