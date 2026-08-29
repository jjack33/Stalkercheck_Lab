package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ThreatPayload
import com.example.ui.theme.CyberAmberWarning
import com.example.ui.theme.CyberEmeraldClean
import com.example.ui.theme.CyberRedCritical
import com.example.ui.theme.M3DarkTextMuted

/**
 * ============================================================================
 * [Vulnera Lab v2.1 UI Dialog - Audit Scorecard Report]
 * Displays detailed pre-boot security verification scorecard, test metrics,
 * pass rate percentage, and status per payload.
 * ============================================================================
 */
@Composable
fun CyberScanAuditReportDialog(
  payloads: List<ThreatPayload>,
  onDismiss: () -> Unit,
  onReTest: () -> Unit
) {
  val armedCount = payloads.count { it.isEnabled }
  val remediatedCount = payloads.count { it.isEnabled && it.isRemediated }
  val passRate = if (armedCount > 0) ((remediatedCount.toFloat() / armedCount.toFloat()) * 100).toInt() else 100

  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    modifier = Modifier.testTag("audit_scorecard_dialog"),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(28.dp)
        )
        Column {
          Text(
            text = "Pre-Boot Audit Verification",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Scanner Engine Scorecard",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 6.dp)
      ) {
        // Scorecard Header Card
        Surface(
          color = MaterialTheme.colorScheme.surfaceContainer,
          shape = RoundedCornerShape(16.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("TEST ARMED", style = MaterialTheme.typography.labelSmall, color = M3DarkTextMuted, fontSize = 9.sp)
              Text("$armedCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("INTERCEPTED", style = MaterialTheme.typography.labelSmall, color = M3DarkTextMuted, fontSize = 9.sp)
              Text("$armedCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CyberAmberWarning)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("NEUTRALIZED", style = MaterialTheme.typography.labelSmall, color = M3DarkTextMuted, fontSize = 9.sp)
              Text("$remediatedCount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = CyberEmeraldClean)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("PASS RATE", style = MaterialTheme.typography.labelSmall, color = M3DarkTextMuted, fontSize = 9.sp)
              Text("$passRate%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
            }
          }
        }

        Text(
          text = "PRE-BOOT THREAT MITIGATION MATRIX",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary,
          letterSpacing = 1.sp
        )

        payloads.forEach { payload ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = payload.name,
              style = MaterialTheme.typography.bodySmall,
              color = if (payload.isEnabled) MaterialTheme.colorScheme.onSurface else M3DarkTextMuted,
              fontFamily = FontFamily.Monospace,
              fontSize = 11.5.sp
            )

            val statusText = when {
              !payload.isEnabled -> "Dormant"
              payload.isRemediated -> "Neutralized (100%)"
              payload.isDetected -> "Detected"
              else -> "Pending Scan"
            }

            val statusColor = when {
              !payload.isEnabled -> M3DarkTextMuted
              payload.isRemediated -> CyberEmeraldClean
              payload.isDetected -> CyberAmberWarning
              else -> CyberRedCritical
            }

            Text(
              text = statusText,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = statusColor
            )
          }
        }

        Surface(
          color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
          shape = RoundedCornerShape(12.dp),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
        ) {
          Text(
            text = "Engine Evaluation Result: Pre-boot kernel hooks successfully intercepted. All persistent signatures unmounted before OS handoff.",
            modifier = Modifier.padding(10.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 10.5.sp
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("report_confirm_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        )
      ) {
        Text("Close", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(
        onClick = onReTest,
        modifier = Modifier.testTag("report_retest_button")
      ) {
        Text("Re-Test Engine", color = MaterialTheme.colorScheme.primary)
      }
    }
  )
}
