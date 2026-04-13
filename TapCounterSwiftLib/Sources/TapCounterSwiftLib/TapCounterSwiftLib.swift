public final class TapCounter {
    private var count = 0

    public init() {}

    public func tap() {
        count += 1
    }

    public func reset() {
        count = 0
    }

    public var currentCount: Int {
        count
    }

    public func label() -> String {
        switch count {
        case 0:
            return "Tap me!"
        case 1:
            return "Tapped once!"
        case 2...9:
            return "Tapped \(count) times"
        default:
            return "Wow, \(count) taps!"
        }
    }
}
