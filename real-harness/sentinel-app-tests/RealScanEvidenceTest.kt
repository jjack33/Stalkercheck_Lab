package com.sentinel.app

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sentinel.app.feed.IndicatorFeedStore
import com.sentinel.app.model.Classification
import com.sentinel.app.model.Finding
import com.sentinel.app.platform.SentinelScanService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * On-device validation of Sentinel's REAL detection pipeline.
 *
 * This runs inside the installed internal Sentinel build and calls the production
 * [SentinelScanService] against the genuinely-installed packages on the device
 * (the high-fidelity target `com.sentinel.target` and the inert fixture
 * `com.sentinel.fixture`). Nothing here is simulated: the scanner reads real
 * PackageManager metadata and the RiskEngine classifies it, exactly as the app
 * does when a user taps Scan.
 *
 * Preconditions (installed by the test harness before this runs):
 *   - com.sentinel.target  (high-fidelity benign high-risk target)
 *   - com.sentinel.fixture (inert declaration-only fixture)
 */
@RunWith(AndroidJUnit4::class)
class RealScanEvidenceTest {

    private lateinit var context: Context
    private lateinit var feedStore: IndicatorFeedStore
    private val scanService by lazy { SentinelScanService(context) }

    private val targetPackage = "com.sentinel.target"
    private val fixturePackage = "com.sentinel.fixture"

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        feedStore = IndicatorFeedStore(context)
        // Start every case from the bundled signed baseline (no cached update).
        feedStore.deleteCachedFeed()
    }

    private fun scanFindings(): List<Finding> = scanService.scan().findings

    private fun findingFor(pkg: String): Finding {
        val finding = scanFindings().firstOrNull { it.app.packageName == pkg }
        assertNotNull(
            "Sentinel did not see $pkg. Is it installed and is package visibility enabled?",
            finding,
        )
        return finding!!
    }

    private fun logFinding(label: String, finding: Finding) {
        println("SENTINEL_EVIDENCE | $label | package=${finding.app.packageName}")
        println("SENTINEL_EVIDENCE |   classification=${finding.classification} (${finding.classification.displayName})")
        println("SENTINEL_EVIDENCE |   score=${finding.score}  feedVersion=${finding.feedVersion}  indicatorId=${finding.indicatorId}")
        finding.evidence.forEach { e ->
            println("SENTINEL_EVIDENCE |   [${e.type}] +${e.points}  ${e.title}")
        }
    }

    /** Baseline (no feed match): the target must be a real HIGH-RISK CONFIGURATION. */
    @Test
    fun target_isHighRiskConfiguration_fromRealCapabilities() {
        val target = findingFor(targetPackage)
        logFinding("TARGET / baseline", target)
        assertEquals(
            "Target should be classified HIGH_RISK_CONFIGURATION from its declared capabilities.",
            Classification.HIGH_RISK_CONFIGURATION,
            target.classification,
        )
        assertTrue("Target score should be high (>= 45).", target.score >= 45)
        val titles = target.evidence.map { it.title }
        assertTrue("Expected accessibility capability evidence.", titles.any { it.contains("accessibility", true) })
        assertTrue("Expected device-admin capability evidence.", titles.any { it.contains("device-admin", true) || it.contains("device administration", true) })
        assertTrue("Expected notification-listener capability evidence.", titles.any { it.contains("notification-listener", true) })
    }

    /** Baseline (no feed match): the inert fixture is HIGH-RISK from declarations alone. */
    @Test
    fun fixture_baseline_isHighRiskConfiguration() {
        val fixture = findingFor(fixturePackage)
        logFinding("FIXTURE / baseline", fixture)
        assertTrue(
            "Fixture baseline should be at least HIGH_RISK_CONFIGURATION or NEEDS_REVIEW, never KNOWN_THREAT without a feed.",
            fixture.classification == Classification.HIGH_RISK_CONFIGURATION ||
                fixture.classification == Classification.NEEDS_REVIEW,
        )
        assertTrue(fixture.classification != Classification.KNOWN_THREAT)
    }

    /** Valid signed v3 feed: fixture becomes a verified KNOWN THREAT. */
    @Test
    fun fixture_withValidSignedFeed_isKnownThreat() {
        feedStore.saveVerified(readAsset("feed_valid_v3.json"))
        val fixture = findingFor(fixturePackage)
        logFinding("FIXTURE / valid v3 feed", fixture)
        assertEquals(
            "A current, high-confidence signed indicator match must produce KNOWN_THREAT.",
            Classification.KNOWN_THREAT,
            fixture.classification,
        )
        assertEquals("SENTINEL-INERT-FIXTURE-ONLY", fixture.indicatorId)
    }

    /** Tampered feed: signature fails, Sentinel ignores it and falls back to baseline. */
    @Test
    fun fixture_withTamperedFeed_isNotKnownThreat() {
        feedStore.saveVerified(readAsset("feed_tampered.json"))
        val fixture = findingFor(fixturePackage)
        logFinding("FIXTURE / tampered feed", fixture)
        assertTrue(
            "A tampered feed must never yield KNOWN_THREAT; it is rejected and baseline is used.",
            fixture.classification != Classification.KNOWN_THREAT,
        )
    }

    /** Correctly signed feed but wrong certificate: package-name lead only -> NEEDS REVIEW. */
    @Test
    fun fixture_withWrongCertFeed_isNeedsReview() {
        feedStore.saveVerified(readAsset("feed_wrong_cert.json"))
        val fixture = findingFor(fixturePackage)
        logFinding("FIXTURE / wrong-cert feed", fixture)
        assertEquals(
            "Package matches but certificate does not: must be NEEDS_REVIEW, never KNOWN_THREAT.",
            Classification.NEEDS_REVIEW,
            fixture.classification,
        )
    }

    private fun readAsset(name: String): String =
        InstrumentationRegistry.getInstrumentation().context.assets
            .open(name).bufferedReader().use { it.readText() }
}
