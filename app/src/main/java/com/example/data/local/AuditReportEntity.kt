package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.AuditReport

/**
 * ============================================================================
 * [Vulnera Lab v2.1 Data Layer - Room Entity]
 * Persists pre-boot security evaluation scorecards and threat intercept metrics
 * to local SQLite via Android Room according to Google's official persistence
 * guidelines (https://developer.android.com/training/data-storage/room).
 * ============================================================================
 */
@Entity(tableName = "audit_reports")
data class AuditReportEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val timestampFormatted: String,
  val timestampEpoch: Long,
  val armedCount: Int,
  val detectedCount: Int,
  val remediatedCount: Int,
  val passRatePercentage: Int,
  val durationMs: Long,
  val summaryNote: String
) {
  fun toDomain(): AuditReport = AuditReport(
    id = id,
    timestampFormatted = timestampFormatted,
    timestampEpoch = timestampEpoch,
    armedCount = armedCount,
    detectedCount = detectedCount,
    remediatedCount = remediatedCount,
    passRatePercentage = passRatePercentage,
    durationMs = durationMs,
    summaryNote = summaryNote
  )

  companion object {
    fun fromDomain(report: AuditReport): AuditReportEntity = AuditReportEntity(
      id = report.id,
      timestampFormatted = report.timestampFormatted,
      timestampEpoch = report.timestampEpoch,
      armedCount = report.armedCount,
      detectedCount = report.detectedCount,
      remediatedCount = report.remediatedCount,
      passRatePercentage = report.passRatePercentage,
      durationMs = report.durationMs,
      summaryNote = report.summaryNote
    )
  }
}
