import TapCounterSwiftLib

/// Android/JNI bridge for the shared `TapCounterSwiftLib.TapCounter` logic.
///
/// This target intentionally contains only export glue. All counter
/// behavior lives in `TapCounterSwiftLib`.
public final class TapCounterBridge {
    private let implementation = TapCounterSwiftLib.TapCounter()

    public init() {}

    public func tap() {
        implementation.tap()
    }

    public func reset() {
        implementation.reset()
    }

    public var currentCount: Int {
        implementation.currentCount
    }

    public func label() -> String {
        implementation.label()
    }
}
