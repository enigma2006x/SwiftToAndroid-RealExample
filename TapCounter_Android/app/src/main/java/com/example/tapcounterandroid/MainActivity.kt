package com.example.tapcounterandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapcounterandroid.ui.theme.TapCounterAndroidTheme
import com.tonytrejo.TapCounterAndroidLib.TapCounterBridge
import org.swift.swiftkit.core.SwiftMemoryManagement
class MainActivity : ComponentActivity() {

    companion object {
        init {
            System.loadLibrary("SwiftJava")
            System.loadLibrary("TapCounterAndroidLib")
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TapCounterAndroidTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TapCounterScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TapCounterScreen(modifier: Modifier = Modifier) {
    val tapCounter = remember {
        TapCounterBridge.init(SwiftMemoryManagement.DEFAULT_SWIFT_JAVA_AUTO_ARENA)
    }
    var label by remember { mutableStateOf(tapCounter.label()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "👋",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Build with Compose",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                tapCounter.tap()
                label = tapCounter.label()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tap me")
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                tapCounter.reset()
                label = tapCounter.label()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TapCounterScreenPreview() {
    TapCounterAndroidTheme {
        TapCounterScreen()
    }
}
