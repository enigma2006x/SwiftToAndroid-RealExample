// swift-tools-version: 6.3
import PackageDescription

let package = Package(
    name: "TapCounterLib",
    platforms: [
        .macOS(.v13)
    ],
    products: [
        .library(
            name: "TapCounterLib",
            type: .dynamic,
            targets: ["TapCounterLib"]
        )
    ],
    dependencies: [
        .package(
            url: "https://github.com/swiftlang/swift-java.git",
            branch: "main"
        )
    ],
    targets: [
        .target(
            name: "TapCounterLib",
            dependencies: [
                .product(name: "SwiftJava", package: "swift-java")
            ],
            plugins: [
                .plugin(name: "JExtractSwiftPlugin", package: "swift-java")
            ]
        ),
        .testTarget(
            name: "TapCounterLibTests",
            dependencies: ["TapCounterLib"]
        )
    ]
)
