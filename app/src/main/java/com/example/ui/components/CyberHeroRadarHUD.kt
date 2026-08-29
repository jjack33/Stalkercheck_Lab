package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CyberAmberWarning
import com.example.ui.theme.CyberEmeraldClean
import com.example.ui.theme.CyberRedCritical
import kotlin.math.cos
import kotlin.math.sin

/**
 * ============================================================================
 * [Vulnera Lab v2.1 UI Component - Radar HUD Banner]
 * Renders live canvas telemetry, rotating radar beam, threat indicators, and
 * header branding with edge-to-edge status bar padding.
 * ============================================================================
 */
@Composable
fun CyberHeroRadarHUD(
  activeCount: Int,
  totalCount: Int,
  isScanning: Boolean,
  onReportClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

  val infiniteTransition = rememberInfiniteTransition(label = "radar_rot")
  val radarAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(3500, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "radar_angle"
  )

  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  val primaryColor = MaterialTheme.colorScheme.primary
  val outlineColor = MaterialTheme.colorScheme.outline
  val surfaceLow = MaterialTheme.colorScheme.surfaceContainerLow
  val surfaceHigh = MaterialTheme.colorScheme.surfaceContainerHigh

  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
      .background(
        brush = Brush.verticalGradient(
          colors = listOf(
            surfaceHigh,
            surfaceLow,
            MaterialTheme.colorScheme.background
          )
        )
      )
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          colors = listOf(primaryColor.copy(alpha = 0.5f), Color.Transparent)
        ),
        shape = RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp)
      )
      .padding(top = statusBarPadding + 14.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
  ) {
    Column {
      // Top Navigation / Header Bar
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
              .background(if (activeCount > 0) CyberRedCritical else CyberEmeraldClean)
          )
          Text(
            text = "PRE-BOOT SECURITY LAB",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 1.8.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            modifier = Modifier
              .clip(CircleShape)
              .clickable { onReportClick() }
              .testTag("audit_report_button"),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Assessment,
                contentDescription = "Audit Report Scorecard",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
              )
              Text(
                text = "AUDIT",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(
                brush = Brush.linearGradient(
                  listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "SEC",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.ExtraBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Visual Radar Scanner & Headline Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Pre-Boot Sandbox",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "High-contrast validation suite for simulating pre-boot kernel hooks, stalkerware signatures, and firmware tampered partitions.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 17.sp
          )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Custom Live Interactive Radar Canvas
        Box(
          modifier = Modifier
            .size(116.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .border(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.7f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            // Concentric Radar Rings
            drawCircle(
              color = outlineColor,
              radius = radius * 0.33f,
              center = center,
              style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f)))
            )
            drawCircle(
              color = outlineColor,
              radius = radius * 0.66f,
              center = center,
              style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f)))
            )
            drawCircle(
              color = primaryColor.copy(alpha = 0.35f),
              radius = radius * 0.95f,
              center = center,
              style = Stroke(width = 1.5.dp.toPx())
            )

            // Crosshair lines
            drawLine(
              color = outlineColor,
              start = Offset(center.x, 0f),
              end = Offset(center.x, size.height),
              strokeWidth = 1.dp.toPx()
            )
            drawLine(
              color = outlineColor,
              start = Offset(0f, center.y),
              end = Offset(size.width, center.y),
              strokeWidth = 1.dp.toPx()
            )

            // Rotating Radar Sweep Beam
            rotate(degrees = radarAngle, pivot = center) {
              val sweepBrush = Brush.sweepGradient(
                0.0f to Color.Transparent,
                0.75f to Color.Transparent,
                1.0f to primaryColor.copy(alpha = 0.7f),
                center = center
              )
              drawCircle(
                brush = sweepBrush,
                radius = radius * 0.95f,
                center = center
              )
            }

            // Blip markers for active payloads
            if (activeCount > 0) {
              val angle1 = Math.toRadians(45.0)
              val blipX = (center.x + (radius * 0.55f) * cos(angle1)).toFloat()
              val blipY = (center.y + (radius * 0.55f) * sin(angle1)).toFloat()
              drawCircle(
                color = CyberRedCritical.copy(alpha = pulseAlpha),
                radius = 4.5.dp.toPx(),
                center = Offset(blipX, blipY)
              )
            }
            if (activeCount > 1) {
              val angle2 = Math.toRadians(210.0)
              val blipX2 = (center.x + (radius * 0.7f) * cos(angle2)).toFloat()
              val blipY2 = (center.y + (radius * 0.7f) * sin(angle2)).toFloat()
              drawCircle(
                color = CyberAmberWarning.copy(alpha = pulseAlpha),
                radius = 4.dp.toPx(),
                center = Offset(blipX2, blipY2)
              )
            }
          }

          Icon(
            imageVector = Icons.Default.TrackChanges,
            contentDescription = "Radar Scanner Target Active",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
        }
      }
    }
  }
}
