package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * ============================================================================
 * [Vulnera Lab v2.1 UI Component - Bottom Action Bar]
 * Fixed bottom bar with edge-to-edge insets, evaluation progress bar, and primary
 * triggers for Reset Lab and Simulate Reboot.
 * ============================================================================
 */
@Composable
fun CyberBottomActionBar(
  isSimulating: Boolean,
  progress: Float,
  stepText: String,
  onReset: () -> Unit,
  onSimulateReboot: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = MaterialTheme.colorScheme.surface,
    shadowElevation = 12.dp,
    modifier = modifier
      .fillMaxWidth()
      .padding(WindowInsets.navigationBars.asPaddingValues()),
    border = BorderStroke(
      1.dp,
      Brush.verticalGradient(listOf(MaterialTheme.colorScheme.outline, Color.Transparent))
    )
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      if (isSimulating) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = stepText,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold,
              fontSize = 10.5.sp
            )
            Text(
              text = "${(progress * 100).toInt()}%",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.ExtraBold,
              fontFamily = FontFamily.Monospace
            )
          }

          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.outline
          )
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Reset Button
        OutlinedButton(
          onClick = onReset,
          enabled = !isSimulating,
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .testTag("reset_lab_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
          ),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reset Lab to Baseline Vectors",
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Reset Lab",
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        // Simulate Reboot Pre-Boot Scan
        Button(
          onClick = onSimulateReboot,
          enabled = !isSimulating,
          modifier = Modifier
            .weight(1.5f)
            .height(52.dp)
            .testTag("simulate_reboot_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          )
        ) {
          if (isSimulating) {
            CircularProgressIndicator(
              modifier = Modifier.size(18.dp),
              color = MaterialTheme.colorScheme.onPrimary,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Evaluating Boot...",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onPrimary
            )
          } else {
            Icon(
              imageVector = Icons.Default.RestartAlt,
              contentDescription = "Trigger Pre-Boot Scan and Verification Sequence",
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Simulate Reboot",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onPrimary
            )
          }
        }
      }
    }
  }
}
