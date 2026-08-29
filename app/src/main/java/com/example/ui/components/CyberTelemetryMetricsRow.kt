package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberEmeraldClean
import com.example.ui.theme.CyberRedCritical
import com.example.ui.theme.M3DarkTextMuted

/**
 * ============================================================================
 * [Vulnera Lab v2.1 UI Component - Telemetry Metric Row]
 * Real-time indicators showing active threat count, intercept latency, and quick actions.
 * ============================================================================
 */
@Composable
fun CyberTelemetryMetricsRow(
  activeCount: Int,
  detectedCount: Int,
  remediatedCount: Int,
  onQuickArmAll: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // Metric 1: Armed Vectors
    CyberMetricCard(
      title = "ARMED THREATS",
      value = "$activeCount Vectors",
      valueColor = if (activeCount > 0) CyberRedCritical else CyberEmeraldClean,
      subtitle = if (activeCount > 0) "Injection Active" else "Clean Partition",
      modifier = Modifier.weight(1f)
    )

    // Metric 2: Detection Latency
    CyberMetricCard(
      title = "INTERCEPT LAG",
      value = "38 ms",
      valueColor = MaterialTheme.colorScheme.primary,
      subtitle = "PBL Stage Handshake",
      modifier = Modifier.weight(1f)
    )

    // Metric 3: Quick Action
    Surface(
      modifier = Modifier
        .weight(1f)
        .clip(RoundedCornerShape(16.dp))
        .clickable { onQuickArmAll() }
        .testTag("arm_all_button"),
      color = MaterialTheme.colorScheme.surfaceContainerHigh,
      border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)),
      shape = RoundedCornerShape(16.dp)
    ) {
      Column(
        modifier = Modifier.padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(
          imageVector = Icons.Default.Layers,
          contentDescription = "Arm All Simulated Vectors",
          tint = MaterialTheme.colorScheme.secondary,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "ARM ALL",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.secondary,
          fontSize = 11.sp
        )
        Text(
          text = "Batch Inject",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 9.sp
        )
      }
    }
  }
}

@Composable
fun CyberMetricCard(
  title: String,
  value: String,
  valueColor: Color,
  subtitle: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    color = MaterialTheme.colorScheme.surfaceContainer,
    shape = RoundedCornerShape(16.dp),
    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = M3DarkTextMuted,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = valueColor,
        fontSize = 15.sp
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 10.sp
      )
    }
  }
}
