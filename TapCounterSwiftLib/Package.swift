// swift-tools-version: 6.0
import PackageDescription

let package = Package(
    name: "TapCounterSwiftLib",
    platforms: [
        .iOS(.v17),
        .macOS(.v13)
    ],
    products: [
        .library(
            name: "TapCounterSwiftLib",
            targets: ["TapCounterSwiftLib"]
        ),
    ],
    targets: [
        .target(
            name: "TapCounterSwiftLib"
        ),
        .testTarget(
            name: "TapCounterSwiftLibTests",
            dependencies: ["TapCounterSwiftLib"]
        ),
    ]
)
