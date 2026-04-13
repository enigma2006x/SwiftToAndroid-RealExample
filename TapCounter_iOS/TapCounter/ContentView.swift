import SwiftUI
import TapCounterSwiftLib

// MARK: - ContentView
struct ContentView: View {
    
    private let tapCounter = TapCounter()
    
    @State private var label: String = "Tap me!"
    
    var body: some View {
        VStack(spacing: 20) {
            Text("👋")
                .font(.system(size: 52))

            Text(label)
                .font(.title)
                .fontWeight(.medium)

            Text("Built with SwiftUI")
                .foregroundStyle(.secondary)

            Button("Tap me") {
                tapCounter.tap()
                updateData()
            }
            .buttonStyle(.borderedProminent)

            Button("Reset") {
                tapCounter.reset()
                updateData()
            }
            .buttonStyle(.bordered)
        }
        .padding()
    }
    
    private func updateData() {
        label = tapCounter.label()
    }
}

#Preview {
    ContentView()
}
