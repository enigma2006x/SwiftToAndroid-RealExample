// Pure Swift model — no @_cdecl needed.
// swift-java's JExtract plugin reads this and auto-generates
// Java/Kotlin wrapper classes + JNI glue at build time.

public final class TapCounter {
    private var count: Int = 0

    public init() {}

    /// Increment and return the new count
    public func tap() -> Int {
        count += 1
        return count
    }

    /// Reset to zero
    public func reset() {
        count = 0
    }

    /// Current count without incrementing
    public var currentCount: Int { count }

    /// Human-readable label for the UI
    public func label() -> String {
        switch count {
        case 0:      return "Tap me!"
        case 1:      return "Tapped once!"
        case 2...9: return "Tapped \(count) times"
        default:    return "Wow, \(count) taps!"
        }
    }
}
