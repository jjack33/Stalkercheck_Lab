package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ConsoleLog
import com.example.domain.model.LogLevel
import com.example.ui.theme.CyberAmberWarning
import com.example.ui.theme.CyberEmeraldClean
import com.example.ui.theme.CyberPurpleKernel
import com.example.ui.theme.CyberRedCritical
import com.example.ui.theme.M3DarkTextMuted
import com.example.ui.theme.M3TerminalBg
import com.example.ui.theme.M3TerminalCard

/**
 * ============================================================================
 * [Vulnera Lab v2.1 UI Component - Terminal HUD Console]
 * High-definition Monospace terminal displaying live bootloader hooks, attestation
 * handshakes, and quarantine logs.
 * ============================================================================
 */
@Composable
fun CyberTerminalHUD(
  logs: List<ConsoleLog>,
  isScanning: Boolean,
  onClearLogs: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "terminal_pulse")
  val cursorAlpha by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(500),
      repeatMode = RepeatMode.Reverse
    ),
    label = "cursor_blink"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .shadow(6.dp, RoundedCornerShape(22.dp)),
    color = M3TerminalBg,
    shape = RoundedCornerShape(22.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Header Top Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(if (isScanning) CyberAmberWarning else CyberEmeraldClean)
          )
          Text(
            text = if (isScanning) "SCANNER STREAM ACTIVE" else "KERNEL CONSOLE STREAM",
            style = MaterialTheme.typography.labelSmall,
            color = if (isScanning) CyberAmberWarning else CyberEmeraldClean,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
          )
        }

        IconButton(
          onClick = onClearLogs,
          modifier = Modifier
            .size(26.dp)
            .testTag("clear_terminal_logs_button")
        ) {
          Icon(
            imageVector = Icons.Default.DeleteSweep,
            contentDescription = "Flush Console Log Buffer",
            tint = M3DarkTextMuted,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      // Terminal Box
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(M3TerminalCard, RoundedCornerShape(14.dp))
          .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
          .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val recentLogs = logs.takeLast(7)
        recentLogs.forEach { log ->
          CyberLogLine(log)
        }

        // Live blinking cursor line
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = "> daemon:listening_preboot_port",
            color = M3DarkTextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
          Box(
            modifier = Modifier
              .size(6.dp, 12.dp)
              .background(MaterialTheme.colorScheme.primary.copy(alpha = cursorAlpha))
          )
        }
      }
    }
  }
}

@Composable
fun CyberLogLine(log: ConsoleLog) {
  val levelColor = when (log.level) {
    LogLevel.ALERT -> CyberRedCritical
    LogLevel.WARN -> CyberAmberWarning
    LogLevel.SUCCESS -> CyberEmeraldClean
    LogLevel.KERNEL -> CyberPurpleKernel
    LogLevel.BOOTLOADER -> MaterialTheme.colorScheme.primary
    LogLevel.SCANNER -> MaterialTheme.colorScheme.primary
    LogLevel.INFO -> MaterialTheme.colorScheme.onSurfaceVariant
  }

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(6.dp),
    verticalAlignment = Alignment.Top
  ) {
    Text(
      text = log.timestamp,
      color = M3DarkTextMuted,
      fontSize = 9.5.sp,
      fontFamily = FontFamily.Monospace
    )

    Text(
      text = "[${log.subsystem}]",
      color = levelColor,
      fontSize = 9.5.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace
    )

    Text(
      text = log.message,
      color = if (log.level == LogLevel.ALERT) CyberRedCritical else MaterialTheme.colorScheme.onSurface,
      fontSize = 10.5.sp,
      fontFamily = FontFamily.Monospace,
      lineHeight = 15.sp,
      modifier = Modifier.weight(1f)
    )
  }
}
