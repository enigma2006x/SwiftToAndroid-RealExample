import Testing
@testable import TapCounterSwiftLib

@Test func tapCounterStartsAtZero() {
    let counter = TapCounter()

    #expect(counter.currentCount == 0)
    #expect(counter.label() == "Tap me!")
}

@Test func tapCounterIncrementsAndResets() {
    let counter = TapCounter()

    #expect(counter.tap() == 1)
    #expect(counter.tap() == 2)
    #expect(counter.currentCount == 2)
    #expect(counter.label() == "Tapped 2 times")

    counter.reset()

    #expect(counter.currentCount == 0)
    #expect(counter.label() == "Tap me!")
}
