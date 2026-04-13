// swift-tools-version: 6.3
import PackageDescription

let package = Package(
    name: "TapCounterAndroidLib",
    platforms: [
        .iOS(.v17),
        .macOS(.v13)
    ],
    products: [
        .library(
            name: "TapCounterAndroidLib",
            type: .dynamic,
            targets: ["TapCounterAndroidLib"]
        ),
    ],
    dependencies: [
        .package(path: "../TapCounterSwiftLib"),
        .package(
            url: "https://github.com/swiftlang/swift-java.git",
            branch: "main"
        ),
    ],
    targets: [
        .target(
            name: "TapCounterAndroidLib",
            dependencies: [
                .product(name: "TapCounterSwiftLib", package: "tapcounterswiftlib"),
                .product(name: "SwiftJava", package: "swift-java"),
            ],
            plugins: [
                .plugin(name: "JExtractSwiftPlugin", package: "swift-java"),
            ]
        ),
        .testTarget(
            name: "TapCounterAndroidLibTests",
            dependencies: ["TapCounterAndroidLib"]
        ),
    ]
)
