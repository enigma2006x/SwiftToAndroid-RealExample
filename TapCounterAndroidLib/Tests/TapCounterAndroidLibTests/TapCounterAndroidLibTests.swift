import Testing
@testable import TapCounterAndroidLib

@Test func tapCounterBridgeDelegatesToSharedLogic() {
    let counterBridge = TapCounterBridge()

    #expect(counterBridge.currentCount == 0)
    #expect(counterBridge.tap() == 1)
    #expect(counterBridge.tap() == 2)
    #expect(counterBridge.currentCount == 2)
    #expect(counterBridge.label() == "Tapped 2 times")

    counterBridge.reset()

    #expect(counterBridge.currentCount == 0)
    #expect(counterBridge.label() == "Tap me!")
}
