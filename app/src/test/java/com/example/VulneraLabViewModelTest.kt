package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.VulneraDatabase
import com.example.data.repository.VulneraRepositoryImpl
import com.example.ui.viewmodel.VulneraLabViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * ============================================================================
 * [Vulnera Lab v2.1 Unit & Integration Test Suite]
 * Tests ViewModel state transitions, simulated threat vectors, and Room DB
 * according to Android Developer Testing guidelines (https://developer.android.com/training/testing).
 * ============================================================================
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class VulneraLabViewModelTest {

  private lateinit var application: Application
  private lateinit var database: VulneraDatabase
  private lateinit var repository: VulneraRepositoryImpl
  private lateinit var viewModel: VulneraLabViewModel

  @Before
  fun setUp() {
    application = ApplicationProvider.getApplicationContext()
    database = VulneraDatabase.getInstance(application)
    repository = VulneraRepositoryImpl(database.auditReportDao())
    viewModel = VulneraLabViewModel(application, repository)
  }

  @Test
  fun initialState_loadsBaselinePayloadsAndConsoleLogs() {
    val state = viewModel.uiState.value
    assertTrue("Should load baseline payloads", state.payloads.isNotEmpty())
    assertTrue("Should have initial system logs", state.logs.isNotEmpty())
    assertEquals("Default category filter is All", "All", state.selectedCategory)
    assertFalse("Should not be simulating at start", state.isSimulating)
  }

  @Test
  fun togglePayload_updatesStateAndEmitsLog() {
    val initialArmed = viewModel.uiState.value.payloads.first { it.id == "stalkerware_gps_mic" }.isEnabled
    viewModel.onTogglePayload("stalkerware_gps_mic", !initialArmed)

    val updatedPayload = viewModel.uiState.value.payloads.first { it.id == "stalkerware_gps_mic" }
    assertEquals(!initialArmed, updatedPayload.isEnabled)
    assertTrue(viewModel.uiState.value.logs.any { it.message.contains("stalkerware_gps_mic") || it.message.contains("Stalkerware") })
  }

  @Test
  fun armAllPayloads_enablesAllVectors() {
    viewModel.onArmAllPayloads()
    val allEnabled = viewModel.uiState.value.payloads.all { it.isEnabled }
    assertTrue("All payloads should be enabled", allEnabled)
    assertEquals(viewModel.uiState.value.payloads.size, viewModel.uiState.value.activePayloadsCount)
  }

  @Test
  fun resetLabBaseline_restoresDefaultState() {
    viewModel.onArmAllPayloads()
    viewModel.onResetLabBaseline()

    val state = viewModel.uiState.value
    assertFalse(state.isSimulating)
    assertEquals(2, state.activePayloadsCount)
  }

  @Test
  fun filterCategory_correctlyFiltersViewList() {
    viewModel.onSelectCategory("Spyware")
    val filtered = viewModel.uiState.value.filteredPayloads
    assertTrue(filtered.all { it.typeFilter == "Spyware" })
  }
}
