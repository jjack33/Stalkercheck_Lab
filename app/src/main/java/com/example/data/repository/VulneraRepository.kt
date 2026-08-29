package com.example.data.repository

import com.example.data.local.AuditReportDao
import com.example.data.local.AuditReportEntity
import com.example.domain.model.AuditReport
import com.example.domain.model.ThreatPayload
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import com.example.ui.theme.CyberAmberWarning
import com.example.ui.theme.CyberCyanAccent
import com.example.ui.theme.CyberPurpleKernel
import com.example.ui.theme.CyberRedCritical
import com.example.ui.theme.CyberRoseExploit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * ============================================================================
 * [Vulnera Lab v2.1 Repository Pattern Interface]
 * Decouples domain logic and UI state from local Room persistence and vector
 * catalogs according to Android Architecture best practices.
 * ============================================================================
 */
interface VulneraRepository {
  fun getAllAuditReports(): Flow<List<AuditReport>>
  fun getLatestAuditReport(): Flow<AuditReport?>
  suspend fun saveAuditReport(report: AuditReport): Long
  suspend fun clearAuditHistory()
  fun getBaselineThreatPayloads(): List<ThreatPayload>
}

class VulneraRepositoryImpl(
  private val auditReportDao: AuditReportDao
) : VulneraRepository {

  override fun getAllAuditReports(): Flow<List<AuditReport>> {
    return auditReportDao.getAllAuditReports().map { entities ->
      entities.map { it.toDomain() }
    }
  }

  override fun getLatestAuditReport(): Flow<AuditReport?> {
    return auditReportDao.getLatestAuditReport().map { it?.toDomain() }
  }

  override suspend fun saveAuditReport(report: AuditReport): Long = withContext(Dispatchers.IO) {
    auditReportDao.insertAuditReport(AuditReportEntity.fromDomain(report))
  }

  override suspend fun clearAuditHistory(): Unit = withContext(Dispatchers.IO) {
    auditReportDao.clearAllAuditReports()
  }

  override fun getBaselineThreatPayloads(): List<ThreatPayload> {
    return listOf(
      ThreatPayload(
        id = "stalkerware_gps_mic",
        name = "Stalkerware-v3.pkg",
        category = "Covert GPS & Audio Streamer",
        typeFilter = "Stalkerware",
        signatureCode = "SIG-STLK-GPSMIC-9942",
        hexSignature = "E2 00 8B 43 7F A1 0C 44 9B",
        mockFilePath = "/system/bin/app_process_hook.so",
        description = "Simulates unauthorized background telemetry, location tracking, and audio beacon mock signature.",
        severity = "CRITICAL",
        mitreAttckRef = "T1430 / T1429 (Location / Audio Capture)",
        icon = Icons.Default.GpsFixed,
        badgeColor = CyberRedCritical,
        isEnabled = true,
        isDetected = false,
        isRemediated = false
      ),
      ThreatPayload(
        id = "persistent_root_hook",
        name = "Persistent-Root.sh",
        category = "Pre-Boot Init.d Hijack",
        typeFilter = "Rootkit & Hook",
        signatureCode = "SIG-ROOT-PREBOOT-8810",
        hexSignature = "7F 45 4C 46 02 01 01 00 11",
        mockFilePath = "/system/etc/init.d/99_recovery_hook.sh",
        description = "Simulates pre-OS boot scripts attempting kernel initialization hook persistence and mount hijack.",
        severity = "CRITICAL",
        mitreAttckRef = "T1402 / T1398 (Bootkit / Persistence)",
        icon = Icons.Default.Lock,
        badgeColor = CyberAmberWarning,
        isEnabled = true,
        isDetected = false,
        isRemediated = false
      ),
      ThreatPayload(
        id = "amtso_standard_test",
        name = "AMTSO-Compliance.tst",
        category = "AMTSO Security Benchmark",
        typeFilter = "AMTSO Standard",
        signatureCode = "AMTSO:TEST-FEATURE-01:VALIDATION",
        hexSignature = "41 4D 54 53 4F 5F 54 45 53 54",
        mockFilePath = "/data/local/tmp/amtso_test_marker.bin",
        description = "Standard benign test payload created by Anti-Malware Testing Standards Org to verify scanner precision without harm.",
        severity = "BENIGN TEST",
        mitreAttckRef = "AMTSO-AV-01 Standard",
        icon = Icons.Default.Security,
        badgeColor = CyberPurpleKernel,
        isEnabled = false,
        isDetected = false,
        isRemediated = false
      ),
      ThreatPayload(
        id = "eicar_standard_sig",
        name = "EICAR-Standard.sig",
        category = "Standard AV Verification Marker",
        typeFilter = "AMTSO Standard",
        signatureCode = "X5O!P%@AP[4\\PZX54(P^)7CC)7}\$EICAR-TEST",
        hexSignature = "58 35 4F 21 50 25 40 41 50 5B",
        mockFilePath = "/sdcard/Download/eicar_test_com.txt",
        description = "Internationally accepted non-malicious ASCII pattern for testing anti-malware response mechanisms.",
        severity = "BENIGN TEST",
        mitreAttckRef = "EICAR Test Standard",
        icon = Icons.Default.Code,
        badgeColor = CyberCyanAccent,
        isEnabled = false,
        isDetected = false,
        isRemediated = false
      ),
      ThreatPayload(
        id = "keylogger_acc_abuse",
        name = "Keylog-Intercept.mock",
        category = "Accessibility Service Tap Logger",
        typeFilter = "Spyware",
        signatureCode = "SIG-INJECT-KEYHOOK-3312",
        hexSignature = "3C 61 63 63 65 73 73 69 62 6C",
        mockFilePath = "/data/system/users/0/accessibility_mock.xml",
        description = "Simulates an abusive accessibility service capturing mock user tap gestures and PIN inputs.",
        severity = "HIGH",
        mitreAttckRef = "T1417 (Input Capture)",
        icon = Icons.Default.Visibility,
        badgeColor = CyberRoseExploit,
        isEnabled = false,
        isDetected = false,
        isRemediated = false
      ),
      ThreatPayload(
        id = "firmware_nvram_tamper",
        name = "NVRAM-Partition-Mod.bin",
        category = "Baseband Firmware Injection",
        typeFilter = "Rootkit & Hook",
        signatureCode = "SIG-NVRAM-BASEBAND-5021",
        hexSignature = "4E 56 52 41 4D 5F 4D 4F 44 00",
        mockFilePath = "/dev/block/by-name/nvram_mock",
        description = "Simulates tampering in protected partition blocks to verify bootloader signature attestation.",
        severity = "CRITICAL",
        mitreAttckRef = "T1398 (Firmware Modification)",
        icon = Icons.Default.BugReport,
        badgeColor = CyberCyanAccent,
        isEnabled = false,
        isDetected = false,
        isRemediated = false
      )
    )
  }
}
