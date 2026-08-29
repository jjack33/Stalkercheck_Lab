package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ui.screens.VulneraSimLabScreen
import com.example.ui.theme.MyApplicationTheme

/**
 * ============================================================================
 * [Vulnera Lab v2.1 Entry Point - MainActivity]
 * Conforms to modern Android Architecture and Jetpack Compose best practices:
 * - Edge-to-edge system insets (enableEdgeToEdge)
 * - Pure Composable screen delegation (VulneraSimLabScreen)
 * - M3 Theme provider (MyApplicationTheme)
 * ============================================================================
 */
class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        VulneraSimLabScreen()
      }
    }
  }
}
