package com.example.ui.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CyberBottomActionBar
import com.example.ui.components.CyberCategoryFilterRow
import com.example.ui.components.CyberHeroRadarHUD
import com.example.ui.components.CyberTelemetryMetricsRow
import com.example.ui.components.CyberTerminalHUD
import com.example.ui.components.CyberThreatPayloadCard
import com.example.ui.dialogs.CyberPayloadInspectorDialog
import com.example.ui.dialogs.CyberScanAuditReportDialog
import com.example.ui.viewmodel.VulneraLabViewModel

/**
 * ============================================================================
 * [Vulnera Lab v2.1 Screen Architecture]
 * Production-ready Jetpack Compose screen implementing Unidirectional Data Flow
 * (UDF) and observing ViewModel state via collectAsStateWithLifecycle() according
 * to Android Architecture guidelines (https://developer.android.com/topic/architecture).
 * ============================================================================
 */
@Composable
fun VulneraSimLabScreen(
  modifier: Modifier = Modifier,
  viewModel: VulneraLabViewModel = viewModel(
    factory = VulneraLabViewModel.provideFactory(
      LocalContext.current.applicationContext as Application
    )
  )
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val listState = rememberLazyListState()

  val categories = remember {
    listOf("All", "Stalkerware", "Rootkit & Hook", "AMTSO Standard", "Spyware")
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    bottomBar = {
      CyberBottomActionBar(
        isSimulating = uiState.isSimulating,
        progress = uiState.simulationProgress,
        stepText = uiState.simulationStepText,
        onReset = { viewModel.onResetLabBaseline() },
        onSimulateReboot = { viewModel.triggerSimulatedReboot() }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
    ) {
      LazyColumn(
        state = listState,
        modifier = Modifier
          .fillMaxSize()
          .padding(bottom = innerPadding.calculateBottomPadding()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // 1. Hero Banner with Radar Scanner HUD
        item {
          CyberHeroRadarHUD(
            activeCount = uiState.activePayloadsCount,
            totalCount = uiState.payloads.size,
            isScanning = uiState.isSimulating,
            onReportClick = { viewModel.onShowReportDialog(true) }
          )
        }

        // 2. Telemetry Metric HUD Cards
        item {
          CyberTelemetryMetricsRow(
            activeCount = uiState.activePayloadsCount,
            detectedCount = uiState.detectedCount,
            remediatedCount = uiState.remediatedCount,
            onQuickArmAll = { viewModel.onArmAllPayloads() }
          )
        }

        // 3. Category Filter Bar
        item {
          CyberCategoryFilterRow(
            categories = categories,
            selectedCategory = uiState.selectedCategory,
            onSelectCategory = { viewModel.onSelectCategory(it) }
          )
        }

        // 4. Threat Payloads Matrix List
        item {
          Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            uiState.filteredPayloads.forEach { payload ->
              CyberThreatPayloadCard(
                payload = payload,
                onToggle = { isChecked ->
                  viewModel.onTogglePayload(payload.id, isChecked)
                },
                onInspect = {
                  viewModel.onInspectPayload(payload)
                }
              )
            }
          }
        }

        // 5. High-Tech Cyber Terminal & Real-Time Telemetry
        item {
          Column(
            modifier = Modifier
              .padding(horizontal = 20.dp)
              .padding(bottom = 16.dp)
          ) {
            CyberTerminalHUD(
              logs = uiState.logs,
              isScanning = uiState.isSimulating,
              onClearLogs = { viewModel.onClearLogs() }
            )
          }
        }

        item {
          Spacer(modifier = Modifier.height(12.dp))
        }
      }
    }
  }

  // Inspection Sheet Dialog
  uiState.inspectedPayload?.let { payload ->
    CyberPayloadInspectorDialog(
      payload = payload,
      onDismiss = { viewModel.onDismissInspect() },
      onToggle = { isChecked ->
        viewModel.onTogglePayload(payload.id, isChecked)
      }
    )
  }

  // Pre-boot Security Audit Report Dialog
  if (uiState.showReportDialog) {
    CyberScanAuditReportDialog(
      payloads = uiState.payloads,
      onDismiss = { viewModel.onShowReportDialog(false) },
      onReTest = {
        viewModel.onShowReportDialog(false)
        viewModel.triggerSimulatedReboot()
      }
    )
  }
}
