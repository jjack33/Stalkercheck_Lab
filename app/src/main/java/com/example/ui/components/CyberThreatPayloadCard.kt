package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.model.ThreatPayload
import com.example.ui.theme.CyberAmberWarning
import com.example.ui.theme.CyberEmeraldClean
import com.example.ui.theme.M3DarkTextMuted

/**
 * ============================================================================
 * [Vulnera Lab v2.1 UI Component - Threat Vector Card]
 * Renders individual simulated threat vector with activation switch, status
 * badges, target partition paths, and code inspection modal trigger.
 * ============================================================================
 */
@Composable
fun CyberThreatPayloadCard(
  payload: ThreatPayload,
  onToggle: (Boolean) -> Unit,
  onInspect: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isArmed = payload.isEnabled

  val cardBorderColor by animateColorAsState(
    targetValue = when {
      payload.isRemediated -> CyberEmeraldClean
      payload.isDetected -> CyberAmberWarning
      isArmed -> payload.badgeColor.copy(alpha = 0.9f)
      else -> MaterialTheme.colorScheme.outline
    },
    label = "border_color"
  )

  Surface(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(20.dp))
      .border(1.dp, cardBorderColor, RoundedCornerShape(20.dp))
      .testTag("payload_card_${payload.id}"),
    color = if (isArmed) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer,
    shape = RoundedCornerShape(20.dp),
    shadowElevation = if (isArmed) 4.dp else 1.dp
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Top Row: Icon + Payload Title + Toggle Switch
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Icon Container with Neon Glow Background
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(payload.badgeColor.copy(alpha = 0.22f))
              .border(1.2.dp, payload.badgeColor.copy(alpha = 0.7f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = payload.icon,
              contentDescription = "${payload.name} category icon",
              tint = payload.badgeColor,
              modifier = Modifier.size(22.dp)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = payload.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace
              )
              if (payload.isRemediated) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Threat Neutralized via Pre-Boot Sweep",
                  tint = CyberEmeraldClean,
                  modifier = Modifier.size(16.dp)
                )
              }
            }
            Text(
              text = payload.category,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.5.sp
            )
          }
        }

        // Custom High-Contrast Switch
        Switch(
          checked = payload.isEnabled,
          onCheckedChange = onToggle,
          modifier = Modifier.testTag("switch_${payload.id}"),
          colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = payload.badgeColor,
            uncheckedThumbColor = M3DarkTextMuted,
            uncheckedTrackColor = MaterialTheme.colorScheme.outline
          )
        )
      }

      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

      // Bottom Row: Target Path & Details Trigger
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "TARGET PATH",
            style = MaterialTheme.typography.labelSmall,
            color = M3DarkTextMuted,
            fontSize = 9.sp,
            letterSpacing = 1.sp
          )
          Text(
            text = payload.mockFilePath,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
        }

        // Inspect Button
        Surface(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onInspect() }
            .testTag("inspect_btn_${payload.id}"),
          color = MaterialTheme.colorScheme.surfaceContainerHighest,
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Code,
              contentDescription = "Inspect Signature Payload",
              tint = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.size(12.dp)
            )
            Text(
              text = "INSPECT",
              color = MaterialTheme.colorScheme.onSurface,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
