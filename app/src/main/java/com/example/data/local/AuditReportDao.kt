package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * ============================================================================
 * [Vulnera Lab v2.1 Data Layer - Room DAO]
 * Reactive data access interface returning Flow<List<AuditReportEntity>> to
 * enable observable unidirectional data flow (UDF).
 * ============================================================================
 */
@Dao
interface AuditReportDao {

  @Query("SELECT * FROM audit_reports ORDER BY timestampEpoch DESC")
  fun getAllAuditReports(): Flow<List<AuditReportEntity>>

  @Query("SELECT * FROM audit_reports ORDER BY timestampEpoch DESC LIMIT 1")
  fun getLatestAuditReport(): Flow<AuditReportEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAuditReport(report: AuditReportEntity): Long

  @Query("DELETE FROM audit_reports WHERE id = :id")
  suspend fun deleteAuditReportById(id: Long)

  @Query("DELETE FROM audit_reports")
  suspend fun clearAllAuditReports()
}
