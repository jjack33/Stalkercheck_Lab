# StalkerCheck Lab — Validation Report

**Repo:** https://github.com/jjack33/Stalkercheck_Lab (cloned to `extracted\stalkercheck_lab`, validated 2026-08-29)
**Question answered:** *"Can I test Sentinel in this Lab?"*

## Verdict

**Not in the way the Lab's description implies — but yes in one specific, useful role.**

The Lab describes itself as "a test lab and threat signature simulator for evaluating stalkerware… detection engines," but it has **no mechanism to interact with, observe, or grade any external scanner**. It cannot exercise Sentinel's detection path, and Sentinel cannot detect anything inside it. However, it is genuinely valuable as a **false-positive (negative) control**: an app dripping with threatening branding that a correct scanner must NOT flag. Recommendation: keep it in the test plan in that role, alongside the purpose-built fixture as the positive control.

## What the Lab actually is (verified in source)

- A single-screen Compose app (`com.aistudio.secsimlab.ktuvwx`) with a cyberpunk "pre-boot scanner" HUD.
- Its "threat payloads" are **hardcoded fictional records** (fake names like `Stalkerware-v3.pkg`, fake hex signatures, fake file paths like `/system/bin/app_process_hook.so`, MITRE ATT&CK reference strings). They exist only as Kotlin data in `VulneraRepository.kt`.
- Its "scan" (`VulneraLabViewModel.triggerSimulatedReboot`) is a **scripted 6-stage animation**: fixed `delay()`s, log lines like "Analyzing… against 1,420+ stalkerware hashes," then it marks whichever payloads you toggled on as detected and remediated. No filesystem, package, network, or OS interaction of any kind.
- Results are stored in a local Room DB. Firebase AI/AppCheck/Retrofit are declared as dependencies but **never called** — `metadata.json` mentions a server-side Gemini capability, yet no app code uses it and no `GEMINI_API_KEY` is packaged.
- Source manifest declares **zero permissions and zero services/receivers**. (The built APK gains `INTERNET`/`ACCESS_NETWORK_STATE`/`READ_GSERVICES` and a few components via Firebase library manifest merge — all unused at runtime.)
- It is completely benign: no monitoring, collection, or surveillance code exists anywhere in it.

## Why it cannot exercise Sentinel's detection

Sentinel's scanner reads real package metadata: declared services/receivers and their bind permissions, requested permissions, installer source, and package+certificate identity checked against a signed indicator feed. The Lab exposes none of those signals — its "threats" are pictures on its own screen, invisible to `PackageManager`. Conversely, the Lab's "detection engine" is theater; it does not scan other apps, so it can't evaluate the fixture or anything else either. The two apps simply have no interaction surface.

## Expected (correct) Sentinel result for the Lab — this is the test

| Signal | Lab value | RiskEngine points |
|---|---|---|
| Capability declarations (accessibility/notification/VPN/admin/IME) | none | 0 |
| Sensitive permissions (overlay, install-packages, boot, FG-service) | none | 0 |
| Installer (adb sideload) | unavailable → limitation, never sideloading evidence | 0 |
| Debuggable (debug build) | yes | +5 |
| Feed match | none | 0 |

**Expected classification: `LIKELY_LEGITIMATE` (score ≈ 5), despite the name "StalkerCheck Lab" and its scary UI.** If Sentinel flags it, that's a false-positive bug — this proves Sentinel judges evidence, not branding. Added as step 2 of the acceptance procedure in `INSTALL_AND_TEST_GUIDE.md`.

## Build validation

The repo as originally pushed did **not** build; four things were missing/needed:

1. **No Gradle wrapper jar or gradlew scripts** — supplied `gradle/wrapper/gradle-wrapper.jar` (version-agnostic launcher). **Now committed to this repo.**
2. **`debug.keystore` missing** — the debug signing config hardcodes `${rootDir}/debug.keystore`; copied this machine's standard Android debug keystore there (keeps one signing identity across all our test APKs).
3. **`local.properties`** — created (escaped-colon form).
4. **SDK platform 36.1** — the Lab uses AGP 9.1.1 / Gradle 9.3.1 / compileSdk 36.1; installed `platforms;android-36.1` via sdkmanager.

Result: `BUILD SUCCESSFUL` (assembleDebug, 39 tasks). Missing `google-services.json` is tolerated (`missingGoogleServicesStrategy = WARN`).

| Artifact | Value |
|---|---|
| APK | `extracted\stalkercheck_lab\app\build\outputs\apk\debug\app-debug.apk` |
| Package / label | `com.aistudio.secsimlab.ktuvwx` / "StalkerCheck Lab" |
| APK SHA-256 | `2ce8f37501e4ea3540ad9216c53cc50818d530bc1b3ab6f2d4854fee18186fa0` |
| Signing cert SHA-256 | `7FD0C87A…CA8F1239` (same debug cert as Sentinel + fixture builds) |

The wrapper jar is now committed, so item 1 is resolved in-repo. Items 2–4 remain machine-local by design: `debug.keystore` and `local.properties` are gitignored (a signing key and a machine-specific SDK path should not be committed), and the SDK platform is an environment prerequisite. A fresh clone therefore still needs a debug keystore — either drop one at the repo root or remove the hardcoded `debugConfig` block so AGP falls back to `~/.android/debug.keystore` automatically.

## Bottom line for your phone session

Install all three: **fixture** (Sentinel must flag it High-risk / Known Threat with the v3 feed), **StalkerCheck Lab** (Sentinel must NOT flag it), and **Sentinel internal**. That pair of opposite expectations is a much stronger acceptance test than either app alone.
