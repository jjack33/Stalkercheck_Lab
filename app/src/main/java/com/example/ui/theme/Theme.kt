package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Current Active Scheme: High-Contrast M3 Cyber Dark Scheme (v2.0)
private val HighContrastCyberDarkScheme = darkColorScheme(
  primary = M3DarkPrimary,
  onPrimary = M3DarkOnPrimary,
  primaryContainer = M3DarkPrimaryContainer,
  onPrimaryContainer = M3DarkOnPrimaryContainer,
  secondary = M3DarkSecondary,
  onSecondary = M3DarkOnSecondary,
  secondaryContainer = M3DarkSecondaryContainer,
  onSecondaryContainer = M3DarkOnSecondaryContainer,
  tertiary = M3DarkTertiary,
  onTertiary = M3DarkOnTertiary,
  tertiaryContainer = M3DarkTertiaryContainer,
  onTertiaryContainer = M3DarkOnTertiaryContainer,
  background = M3DarkBackground,
  onBackground = M3DarkOnBackground,
  surface = M3DarkSurface,
  onSurface = M3DarkOnSurface,
  surfaceVariant = M3DarkSurfaceContainer,
  onSurfaceVariant = M3DarkOnSurfaceVariant,
  surfaceDim = M3DarkSurfaceDim,
  surfaceBright = M3DarkSurfaceBright,
  surfaceContainerLowest = M3DarkSurfaceContainerLowest,
  surfaceContainerLow = M3DarkSurfaceContainerLow,
  surfaceContainer = M3DarkSurfaceContainer,
  surfaceContainerHigh = M3DarkSurfaceContainerHigh,
  surfaceContainerHighest = M3DarkSurfaceContainerHighest,
  outline = M3DarkOutline,
  outlineVariant = M3DarkOutlineVariant,
  error = CyberRedCritical,
  onError = Color.White,
  errorContainer = Color(0xFF4C0519),
  onErrorContainer = Color(0xFFFFD1DC)
)

/*
 * ==========================================
 * [VERSION ARCHIVE / RESTORATION REFERENCE]
 * Legacy V1 Light Theme Scheme:
 * ==========================================
 * private val LegacyLightColorScheme = androidx.compose.material3.lightColorScheme(
 *   primary = LegacyPurplePrimary,
 *   onPrimary = Color.White,
 *   primaryContainer = LegacyPurpleContainer,
 *   onPrimaryContainer = LegacyOnPurpleContainer,
 *   secondary = LegacyPurpleDark,
 *   onSecondary = Color.White,
 *   background = LegacyBackground,
 *   onBackground = Color(0xFF1D1B20),
 *   surface = LegacySurface,
 *   onSurface = Color(0xFF1D1B20),
 *   surfaceVariant = LegacySurfaceVariant,
 *   onSurfaceVariant = Color(0xFF49454F),
 *   outline = LegacyOutline,
 *   outlineVariant = LegacyOutlineVariant,
 * )
 */

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = HighContrastCyberDarkScheme,
    typography = Typography,
    content = content
  )
}


