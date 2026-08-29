# StalkerCheck Lab

This repository contains two different things. Know which one you are looking at.

## 1. Vulnera Sim Lab — a **simulation** (root `app/` module)

`com.aistudio.secsimlab.ktuvwx` — a single-screen Compose app with a cyberpunk
scanner HUD. Its "threat payloads" are hardcoded fictional records in
`VulneraRepository.kt`, and its "scan" is a scripted six-stage `delay()` animation
in `VulneraLabViewModel.triggerSimulatedReboot()`. It performs **no** filesystem,
package, network, or OS inspection, and it has no surface for an external scanner
to interact with.

It is completely benign, and it is **not** a detection-engine test bed. Full
analysis: [`LAB_VALIDATION.md`](LAB_VALIDATION.md).

Its one genuinely useful role is as a **false-positive control**: an app soaked in
threatening branding that a correct scanner must *not* flag. Sentinel should
classify it `LIKELY_LEGITIMATE`.

## 2. `real-harness/` — the **actual** test capability

Real, installable Android packages whose capabilities Android genuinely registers,
plus signed indicator feeds and an on-device instrumentation test that drives
Sentinel's production scan code. No animation, no hardcoded verdicts.

Start here: [`real-harness/README.md`](real-harness/README.md).

| | Package | Role |
|---|---|---|
| Target | `com.sentinel.target` | Real accessibility / notification-listener / device-admin / VPN declarations → expected `HIGH_RISK_CONFIGURATION` |
| Fixture | `com.sentinel.fixture` | Inert; signed-indicator path → expected `KNOWN_THREAT` with the valid v3 feed |
| Sim Lab | `com.aistudio.secsimlab.ktuvwx` | Benign control → expected `LIKELY_LEGITIMATE` |

Those three opposite expectations, run together, are the real acceptance test.

> **Safety:** the target app has the *shape* of stalkerware and none of the
> behavior — every component body is empty, and it requests no location,
> microphone, camera, SMS, or contacts permission. Do not add surveillance
> behavior to it. See the safety section in `real-harness/README.md`.

## Building

The Sim Lab needs a `debug.keystore` at the repo root (gitignored — its
`debugConfig` block hardcodes that path) and a `local.properties` pointing at your
Android SDK. It targets AGP 9.1.1 / Gradle 9.3.1 / compileSdk 36.1.
