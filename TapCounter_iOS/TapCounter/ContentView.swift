import SwiftUI

// MARK: - ContentView
struct ContentView: View {
    var body: some View {
        VStack(spacing: 20) {
            Text("👋")
                .font(.system(size: 52))

            Text("Tap me!")
                .font(.title)
                .fontWeight(.medium)

            Text("Built with SwiftUI")
                .foregroundStyle(.secondary)

            Button("Tap me") {
               
            }
            .buttonStyle(.borderedProminent)

            Button("Reset") {
               
            }
            .buttonStyle(.bordered)
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
