package com.example.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * ============================================================================
 * [Vulnera Lab v2.1 Domain Layer]
 * Implements immutable domain models for security threat vectors, simulated
 * signatures, and telemetry log streams according to Android App Architecture
 * guidelines (https://developer.android.com/topic/architecture/domain-layer).
 * ============================================================================
 */

/**
 * Represents a simulated benign test payload / threat vector used to evaluate
 * early pre-boot scanner detection hooks.
 */
@Immutable
data class ThreatPayload(
  val id: String,
  val name: String,
  val category: String,
  val typeFilter: String,
  val signatureCode: String,
  val hexSignature: String,
  val mockFilePath: String,
  val description: String,
  val severity: String,
  val mitreAttckRef: String,
  val icon: ImageVector,
  val badgeColor: Color,
  val isEnabled: Boolean = false,
  val isDetected: Boolean = false,
  val isRemediated: Boolean = false
)

/**
 * Security log levels corresponding to Android syslog & kernel priority levels.
 */
enum class LogLevel {
  INFO,
  ALERT,
  WARN,
  SUCCESS,
  KERNEL,
  BOOTLOADER,
  SCANNER
}

/**
 * Represents a single structured terminal log entry captured during pre-boot simulation.
 */
@Immutable
data class ConsoleLog(
  val timestamp: String,
  val subsystem: String,
  val message: String,
  val hexOffset: String = "0x7F" + (1000..9999).random().toString(16).uppercase(),
  val level: LogLevel = LogLevel.INFO
)

/**
 * Represents an evaluated pre-boot scan audit scorecard persisted to Room DB.
 */
@Immutable
data class AuditReport(
  val id: Long = 0,
  val timestampFormatted: String,
  val timestampEpoch: Long,
  val armedCount: Int,
  val detectedCount: Int,
  val remediatedCount: Int,
  val passRatePercentage: Int,
  val durationMs: Long,
  val summaryNote: String
)
