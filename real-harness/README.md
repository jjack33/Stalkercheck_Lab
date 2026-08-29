# Sentinel Real Detection Harness

This directory is the **actual** test capability for the Sentinel anti-stalkerware
scanner. It exists because the Vulnera Sim Lab app in the root of this repository
is a **simulation** and cannot test a real scanner (see `../LAB_VALIDATION.md`).

Nothing in this directory is scripted or animated. Every signal here is produced by
a genuinely installed Android package and read back through Android's real
`PackageManager` by Sentinel's production scan code.

---

## Why the Sim Lab could not do this

The root app (`com.example`, "Vulnera Sim Lab") renders hardcoded fictional
`ThreatPayload` records from `VulneraRepository.kt`, and its "scan" is a fixed
six-stage `delay()` animation in `VulneraLabViewModel.triggerSimulatedReboot()`.
It never inspects an installed package and exposes no surface for an external
scanner to observe or be graded against. It is useful only as a **benign
false-positive control**: Sentinel should classify it *Likely Legitimate* despite
its alarming name and UI.

## What replaces it

| Component | Package | What it really is |
|---|---|---|
| `sentinel-target/` | `com.sentinel.target` | **High-fidelity benign target.** Declares a *real*, OS-registerable accessibility service, notification-listener service, device-admin receiver and VPN service, plus overlay / install-packages / boot / foreground-service permissions. Android lists these in Settings and a tester can actually enable them. |
| `sentinel-fixture/` | `com.sentinel.fixture` | **Inert fixture.** Declaration-only components, zero permissions. Used for the signed-indicator (`KNOWN_THREAT`) path. |
| `feeds/` | — | Signed ECDSA P-256 indicator feeds: valid v3, byte-tampered, and wrong-certificate variants. |
| `sentinel-app-tests/` | — | On-device instrumentation test that drives Sentinel's production `SentinelScanService`. |

### Safety boundary — read this

The target app has the **shape** of stalkerware and **none** of the behavior.
Every component body is deliberately empty:

- the accessibility service's `onAccessibilityEvent` / `onInterrupt` do nothing — no screen content is ever read;
- the notification listener's `onNotificationPosted` / `onNotificationRemoved` do nothing — no notification content is ever accessed;
- the VPN service never calls `Builder` or `establish()` — no tunnel exists, no traffic is captured or routed;
- the device-admin receiver enforces no policy;
- the boot receiver starts nothing.

It requests **no** location, microphone, camera, SMS, or contacts permission. It
collects nothing, stores nothing, transmits nothing, and hides nothing: the icon
and label are visible and self-describing. This is the standard way anti-stalkerware
detection is validated — the real detection surface, without the harm. Do not add
surveillance behavior to these apps.

---

## What the harness proves

`sentinel-app-tests/RealScanEvidenceTest.kt` runs *inside* the installed internal
Sentinel build and calls the production `SentinelScanService.scan()` against the
packages actually present on the device. It asserts five real behaviors:

1. **`target_isHighRiskConfiguration_fromRealCapabilities`** — the target is classified `HIGH_RISK_CONFIGURATION` (score ≥ 45) from its genuine declared capabilities.
2. **`fixture_baseline_isHighRiskConfiguration`** — with no matching feed, the fixture is *never* `KNOWN_THREAT`.
3. **`fixture_withValidSignedFeed_isKnownThreat`** — a current, HIGH-confidence signed indicator matching package **and** certificate yields `KNOWN_THREAT` with `indicatorId = SENTINEL-INERT-FIXTURE-ONLY`.
4. **`fixture_withTamperedFeed_isNotKnownThreat`** — a byte-tampered feed fails signature verification, is ignored, and never yields `KNOWN_THREAT`.
5. **`fixture_withWrongCertFeed_isNeedsReview`** — a correctly signed feed whose certificate does not match yields `NEEDS_REVIEW`, never `KNOWN_THREAT`.

Together these exercise the heuristic path, the signed-feed trust path, signature
tampering rejection, and the certificate-pinning boundary — the security-critical
guarantee that a heuristic alone can never produce a "Known threat" verdict.

---

## How to run it

The test is written against Sentinel's source tree, not this repo. Install it into
Sentinel and run against a device or emulator.

```bash
# 1. Build the two target apps
cd real-harness/sentinel-target  && ./gradlew :app:assembleDebug
cd ../sentinel-fixture           && ./gradlew :app:assembleDebug

# 2. Install both on the device/emulator
adb install -r sentinel-target/app/build/outputs/apk/debug/app-debug.apk
adb install -r sentinel-fixture/app/build/outputs/apk/debug/app-debug.apk

# 3. Drop the test + feed assets into the Sentinel tree
cp sentinel-app-tests/RealScanEvidenceTest.kt \
   <sentinel>/app/src/androidTest/java/com/sentinel/app/
mkdir -p <sentinel>/app/src/androidTest/assets
cp feeds/sentinel_fixture_feed_v3.json            <sentinel>/app/src/androidTest/assets/feed_valid_v3.json
cp feeds/sentinel_fixture_feed_v3_tampered.json   <sentinel>/app/src/androidTest/assets/feed_tampered.json
cp feeds/sentinel_fixture_feed_v4_wrong_cert.json <sentinel>/app/src/androidTest/assets/feed_wrong_cert.json

# 4. Run the real scan on-device (internal flavor: has QUERY_ALL_PACKAGES + fixture trust key)
cd <sentinel> && ./gradlew :app:connectedInternalDebugAndroidTest
```

Sentinel's `app/build.gradle.kts` needs these test dependencies:

```kotlin
androidTestImplementation("androidx.test.ext:junit:1.2.1")
androidTestImplementation("androidx.test:runner:1.6.2")
androidTestImplementation("androidx.test:rules:1.6.1")
```

Evidence is printed to logcat with the `SENTINEL_EVIDENCE` prefix — classification,
score, feed version, indicator id, and every scored evidence item per package.

**The `internal` flavor is required.** The `public` flavor deliberately does not
trust the fixture signing key (`INTERNAL_TEST_TRUST_ENABLED=false`) and has no
broad package visibility, so cases 3–5 will not reproduce there. That asymmetry is
itself a designed security property.

---

## Current status — honest

| Item | Status |
|---|---|
| Target app source + manifest | **Complete** |
| Target APK built | **Yes** — `sha256 f70a76d17a944824835bbfb22e159e94185d88b669ffc957e94149cf1750ccec` |
| Target APK audited | **Yes** — declares the 4 bind permissions + 6 requested permissions; **no** location/mic/camera/SMS/contacts |
| Fixture app + signed feeds | **Complete** (regenerated; feed private key destroyed after signing) |
| Instrumentation test | **Written, not yet executed** |
| Emulator execution on this Windows host | **Blocked** — see below |

### Emulator blocker

The Android emulator (`emulator 37.1.11`, AVD `sentinel_test`, `system-images;android-36;google_apis;x86_64`)
**segfaults during graphics initialization** on this host (Windows 11, RTX 4060 Laptop GPU),
exiting 139 before `adbd` ever comes up. Reproduced three times:

- `-gpu swiftshader_indirect` → segfault after `Selecting Vulkan device: SwiftShader Device`
- `-gpu host` → segfault after `Graphics Adapter ... NVIDIA GeForce RTX 4060`
- `-gpu swiftshader_indirect -feature -Vulkan` → segfault ~32 s in

Hypervisor checks pass (`Hypervisor compatibility ... met`), so this is a graphics-stack
crash in the emulator build, not a virtualization problem.

**Therefore the five assertions above are not yet verified.** They are expected
results derived from reading `RiskEngine.kt` and the audited manifest — not observed
ones. Do not cite them as evidence until the run completes.

Paths to unblock:
1. Run on a **physical device** over adb (recommended — also the more meaningful result).
2. Try an older emulator release, or an `android-35` / non-`google_apis` image.
3. Run the emulator from Android Studio, which sometimes supplies a working graphics config.

### Expected target score (from `RiskEngine.kt`)

| Signal | Points |
|---|---|
| Accessibility service declared | +20 |
| Device-admin receiver declared | +20 |
| Notification-listener service declared | +15 |
| VPN service declared | +12 |
| `SYSTEM_ALERT_WINDOW` | +8 |
| `REQUEST_INSTALL_PACKAGES` | +10 |
| `RECEIVE_BOOT_COMPLETED` | +5 |
| `FOREGROUND_SERVICE` | +5 |
| Debuggable build | +5 |
| **Total** | **100** (coerced to 0–100) — well above the 45 `HIGH_RISK_CONFIGURATION` threshold |

---

## Cleanup

Both apps are test-only. Uninstall after testing:

```bash
adb uninstall com.sentinel.target
adb uninstall com.sentinel.fixture
```

If you enabled the target's accessibility service, notification access, or device-admin
role during testing, revoke them in Settings first — device admin must be deactivated
before the package will uninstall.
