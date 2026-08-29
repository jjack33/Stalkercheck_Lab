package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.M3DarkOnSurface
import com.example.ui.theme.M3DarkTextMuted

/**
 * ============================================================================
 * [Vulnera Lab v2.1 UI Dialog - Payload Inspector]
 * Detailed modal inspecting MITRE ATT&CK reference, mock hex dump signature,
 * injection destination, and severity rating.
 * ============================================================================
 */
@Composable
fun CyberPayloadInspectorDialog(
  payload: ThreatPayload,
  onDismiss: () -> Unit,
  onToggle: (Boolean) -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(24.dp),
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    modifier = Modifier.testTag("payload_inspector_dialog"),
    title = {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(payload.badgeColor.copy(alpha = 0.2f))
            .border(1.dp, payload.badgeColor, RoundedCornerShape(10.dp)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = payload.icon,
            contentDescription = null,
            tint = payload.badgeColor,
            modifier = Modifier.size(20.dp)
          )
        }
        Column {
          Text(
            text = payload.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = FontFamily.Monospace
          )
          Text(
            text = payload.category,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }
    },
    text = {
      Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(top = 4.dp)
      ) {
        Text(
          text = payload.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        CyberDetailField(label = "SIGNATURE ID", value = payload.signatureCode, isMono = true)
        CyberDetailField(label = "MOCK HEX DUMP", value = payload.hexSignature, isMono = true, color = CyberAmberWarning)
        CyberDetailField(label = "TARGET INJECTION PATH", value = payload.mockFilePath, isMono = true, color = MaterialTheme.colorScheme.primary)
        CyberDetailField(label = "MITRE ATT&CK REF", value = payload.mitreAttckRef)
        CyberDetailField(label = "SEVERITY RATING", value = payload.severity, color = payload.badgeColor)

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Arm Vector in Pre-Boot Sandbox",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Switch(
            checked = payload.isEnabled,
            onCheckedChange = { onToggle(it) },
            modifier = Modifier.testTag("dialog_switch_${payload.id}"),
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = payload.badgeColor,
              uncheckedThumbColor = M3DarkTextMuted,
              uncheckedTrackColor = MaterialTheme.colorScheme.outline
            )
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("dialog_close_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary
        )
      ) {
        Text("Done", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
      }
    }
  )
}

@Composable
fun CyberDetailField(
  label: String,
  value: String,
  isMono: Boolean = false,
  color: Color = M3DarkOnSurface
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = M3DarkTextMuted,
      fontSize = 9.sp,
      letterSpacing = 1.sp,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall,
      color = color,
      fontWeight = FontWeight.Medium,
      fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default,
      fontSize = if (isMono) 11.sp else 12.sp
    )
  }
}
