package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.Immutable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.VulneraDatabase
import com.example.data.repository.VulneraRepository
import com.example.data.repository.VulneraRepositoryImpl
import com.example.domain.model.AuditReport
import com.example.domain.model.ConsoleLog
import com.example.domain.model.LogLevel
import com.example.domain.model.ThreatPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ============================================================================
 * [Vulnera Lab v2.1 UI State Definition]
 * Follows Unidirectional Data Flow (UDF) according to Android Architecture guidelines
 * (https://developer.android.com/topic/architecture/ui-layer).
 * ============================================================================
 */
@Immutable
data class VulneraUiState(
  val payloads: List<ThreatPayload> = emptyList(),
  val logs: List<ConsoleLog> = emptyList(),
  val selectedCategory: String = "All",
  val isSimulating: Boolean = false,
  val simulationProgress: Float = 0f,
  val simulationStage: Int = 0,
  val simulationStepText: String = "",
  val inspectedPayload: ThreatPayload? = null,
  val showReportDialog: Boolean = false,
  val auditHistory: List<AuditReport> = emptyList(),
  val activeReport: AuditReport? = null
) {
  val activePayloadsCount: Int get() = payloads.count { it.isEnabled }
  val detectedCount: Int get() = payloads.count { it.isEnabled && it.isDetected }
  val remediatedCount: Int get() = payloads.count { it.isEnabled && it.isRemediated }

  val filteredPayloads: List<ThreatPayload> get() {
    return if (selectedCategory == "All") payloads
    else payloads.filter { it.typeFilter == selectedCategory }
  }
}

/**
 * Android Architecture Components ViewModel managing the state of Vulnera Lab.
 */
class VulneraLabViewModel(
  application: Application,
  private val repository: VulneraRepository
) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(
    VulneraUiState(
      payloads = repository.getBaselineThreatPayloads(),
      logs = listOf(
        ConsoleLog(getCurrentTimestamp(), "SYS-BOOT", "Initializing StalkerCheck Lab kernel sandbox v3.4...", level = LogLevel.KERNEL),
        ConsoleLog(getCurrentTimestamp(), "SECURITY", "Pre-boot integrity hook ready. TrustZone verified.", level = LogLevel.INFO),
        ConsoleLog(getCurrentTimestamp(), "INJECT", "Injected [Stalkerware-v3.pkg] into /system/bin (Simulated mock)", level = LogLevel.ALERT),
        ConsoleLog(getCurrentTimestamp(), "INJECT", "Injected [Persistent-Root.sh] pre-boot init hook", level = LogLevel.WARN),
        ConsoleLog(getCurrentTimestamp(), "DAEMON", "Awaiting simulated reboot or scanner trigger signal...", level = LogLevel.INFO)
      )
    )
  )

  val uiState: StateFlow<VulneraUiState> = _uiState.asStateFlow()

  init {
    viewModelScope.launch {
      repository.getAllAuditReports().collect { reports ->
        _uiState.update { it.copy(auditHistory = reports) }
      }
    }
  }

  private fun getCurrentTimestamp(): String {
    val sdf = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    return sdf.format(Date())
  }

  fun onSelectCategory(category: String) {
    _uiState.update { it.copy(selectedCategory = category) }
  }

  fun onTogglePayload(id: String, isChecked: Boolean) {
    _uiState.update { current ->
      val updatedList = current.payloads.map { p ->
        if (p.id == id) p.copy(isEnabled = isChecked, isDetected = false, isRemediated = false)
        else p
      }
      val targetPayload = updatedList.find { it.id == id }
      val newLog = if (targetPayload != null) {
        ConsoleLog(
          getCurrentTimestamp(),
          "VECTOR",
          if (isChecked) "ARMED vector [${targetPayload.name}] at ${targetPayload.mockFilePath}" else "DISARMED [${targetPayload.name}]",
          level = if (isChecked) LogLevel.WARN else LogLevel.INFO
        )
      } else null

      val updatedLogs = if (newLog != null) current.logs + newLog else current.logs
      current.copy(
        payloads = updatedList,
        logs = updatedLogs,
        inspectedPayload = if (current.inspectedPayload?.id == id) targetPayload else current.inspectedPayload
      )
    }
  }

  fun onArmAllPayloads() {
    _uiState.update { current ->
      val armedList = current.payloads.map { it.copy(isEnabled = true, isDetected = false, isRemediated = false) }
      val log = ConsoleLog(
        getCurrentTimestamp(),
        "INJECT-ALL",
        "Armed ALL ${armedList.size} simulated threat vectors into test partition.",
        level = LogLevel.WARN
      )
      current.copy(payloads = armedList, logs = current.logs + log)
    }
  }

  fun onResetLabBaseline() {
    _uiState.update { current ->
      val baseline = repository.getBaselineThreatPayloads()
      val resetLogs = listOf(
        ConsoleLog(getCurrentTimestamp(), "SYS-RESET", "StalkerCheck Lab state reset to baseline defaults.", level = LogLevel.INFO),
        ConsoleLog(getCurrentTimestamp(), "INJECT", "Injected [Stalkerware-v3.pkg] & [Persistent-Root.sh] test markers.", level = LogLevel.INFO),
        ConsoleLog(getCurrentTimestamp(), "ENGINE", "Pre-boot scanner engine ready for evaluation.", level = LogLevel.INFO)
      )
      current.copy(
        payloads = baseline,
        logs = resetLogs,
        isSimulating = false,
        simulationProgress = 0f,
        simulationStage = 0,
        simulationStepText = "",
        inspectedPayload = null,
        showReportDialog = false
      )
    }
  }

  fun onClearLogs() {
    _uiState.update { current ->
      current.copy(
        logs = listOf(ConsoleLog(getCurrentTimestamp(), "SYS", "Console buffer flushed.", level = LogLevel.INFO))
      )
    }
  }

  fun onInspectPayload(payload: ThreatPayload?) {
    _uiState.update { it.copy(inspectedPayload = payload) }
  }

  fun onDismissInspect() {
    _uiState.update { it.copy(inspectedPayload = null) }
  }

  fun onShowReportDialog(show: Boolean) {
    _uiState.update { it.copy(showReportDialog = show) }
  }

  fun triggerSimulatedReboot() {
    if (_uiState.value.isSimulating) return

    viewModelScope.launch(Dispatchers.Default) {
      val startTime = System.currentTimeMillis()
      _uiState.update {
        it.copy(
          isSimulating = true,
          simulationProgress = 0.05f,
          simulationStage = 1,
          simulationStepText = "STAGE 1/6: Hardware BootROM & PBL Verification"
        )
      }
      addLog("PBL-EXEC", ">>> POWER-ON RESET: Triggering pre-boot scan sequence <<<", LogLevel.KERNEL)
      delay(550)

      _uiState.update {
        it.copy(
          simulationProgress = 0.20f,
          simulationStage = 2,
          simulationStepText = "STAGE 2/6: XBL / UEFI Secure Boot Integrity Check"
        )
      }
      addLog("SEC-BOOT", "Attestation key validated. Verifying bootloader signature tree...", LogLevel.BOOTLOADER)
      delay(600)

      _uiState.update {
        it.copy(
          simulationProgress = 0.40f,
          simulationStage = 3,
          simulationStepText = "STAGE 3/6: Pre-Boot Security Engine Early Execution Handshake"
        )
      }
      addLog("SEC-ENGINE", "Executing anti-spyware engine before OS kernel initialization...", LogLevel.SCANNER)
      delay(700)

      _uiState.update {
        it.copy(
          simulationProgress = 0.65f,
          simulationStage = 4,
          simulationStepText = "STAGE 4/6: Deep Partition & Memory Signature Sweep"
        )
      }
      addLog("SCAN-SWEEP", "Analyzing system mount points against 1,420+ stalkerware hashes...", LogLevel.SCANNER)
      delay(800)

      val enabledPayloads = _uiState.value.payloads.filter { it.isEnabled }
      if (enabledPayloads.isEmpty()) {
        addLog("SCAN-RESULT", "Zero threat signatures detected. Clean partition state.", LogLevel.SUCCESS)
      } else {
        enabledPayloads.forEach { payload ->
          _uiState.update { state ->
            state.copy(
              payloads = state.payloads.map {
                if (it.id == payload.id) it.copy(isDetected = true) else it
              }
            )
          }
          addLog("INTERCEPT", "DETECTED THREAT: ${payload.name} at [${payload.mockFilePath}] (${payload.signatureCode})", LogLevel.ALERT)
          delay(350)
        }
      }

      _uiState.update {
        it.copy(
          simulationProgress = 0.85f,
          simulationStage = 5,
          simulationStepText = "STAGE 5/6: Quarantine Enforcement & Hook Remediation"
        )
      }
      delay(600)

      if (enabledPayloads.isNotEmpty()) {
        enabledPayloads.forEach { payload ->
          _uiState.update { state ->
            state.copy(
              payloads = state.payloads.map {
                if (it.id == payload.id) it.copy(isRemediated = true) else it
              }
            )
          }
          addLog("REMEDIATE", "UNMOUNTED & WIPED: ${payload.mockFilePath} (Remediation 100%)", LogLevel.SUCCESS)
          delay(300)
        }
      }

      _uiState.update {
        it.copy(
          simulationProgress = 1.0f,
          simulationStage = 6,
          simulationStepText = "STAGE 6/6: Handoff Clean OS Boot. Security Audit Saved."
        )
      }
      addLog("OS-HANDOFF", "Pre-boot security audit successful. Booting Android OS cleanly.", LogLevel.SUCCESS)
      delay(500)

      val durationMs = System.currentTimeMillis() - startTime
      val armedCount = _uiState.value.activePayloadsCount
      val detected = _uiState.value.detectedCount
      val remediated = _uiState.value.remediatedCount
      val passRate = if (armedCount > 0) ((remediated.toFloat() / armedCount.toFloat()) * 100).toInt() else 100

      val auditReport = AuditReport(
        timestampFormatted = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
        timestampEpoch = System.currentTimeMillis(),
        armedCount = armedCount,
        detectedCount = detected,
        remediatedCount = remediated,
        passRatePercentage = passRate,
        durationMs = durationMs,
        summaryNote = "Pre-boot evaluation completed in ${durationMs}ms. Remediated $remediated / $armedCount threat signatures."
      )

      repository.saveAuditReport(auditReport)

      _uiState.update {
        it.copy(
          isSimulating = false,
          showReportDialog = true,
          activeReport = auditReport
        )
      }
    }
  }

  private fun addLog(subsystem: String, message: String, level: LogLevel) {
    val log = ConsoleLog(getCurrentTimestamp(), subsystem, message, level = level)
    _uiState.update { it.copy(logs = it.logs + log) }
  }

  companion object {
    fun provideFactory(application: Application): ViewModelProvider.Factory =
      object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          val db = VulneraDatabase.getInstance(application)
          val repository = VulneraRepositoryImpl(db.auditReportDao())
          return VulneraLabViewModel(application, repository) as T
        }
      }
  }
}
