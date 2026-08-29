#!/usr/bin/env python3
"""Audit an Android APK: SHA-256, signing certs, permissions, components, assets.

Usage:
    python audit_apk.py <apk> [<apk> ...] [--markdown]

Uses apksigner + aapt2 from build-tools. Prints a per-APK report and, with
--markdown, emits a table suitable for pasting into validation records.
Exit code 0 always (reporting tool, not a gate); pipe to expectation checks.
"""
import hashlib
import re
import subprocess
import sys
from pathlib import Path

BUILD_TOOLS = Path("C:/Users/docja/AppData/Local/Android/Sdk/build-tools/36.0.0")
APKSIGNER = BUILD_TOOLS / "apksigner.bat"
AAPT2 = BUILD_TOOLS / "aapt2.exe"


def run(cmd):
    return subprocess.run(cmd, capture_output=True, text=True, check=False,
                          encoding="utf-8", errors="replace").stdout


def sha256(path):
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1 << 20), b""):
            h.update(chunk)
    return h.hexdigest()


def audit(apk: str) -> dict:
    info = {"apk": apk, "sha256": sha256(apk)}
    certs = run([str(APKSIGNER), "verify", "--print-certs", apk])
    info["cert_sha256"] = re.findall(r"SHA-256 digest: ([0-9a-f]+)", certs)
    badging = run([str(AAPT2), "dump", "badging", apk])
    m = re.search(r"package: name='([^']+)' versionCode='([^']+)' versionName='([^']+)'", badging)
    if m:
        info["package"], info["versionCode"], info["versionName"] = m.groups()
    label = re.search(r"application-label:'([^']*)'", badging)
    info["label"] = label.group(1) if label else ""
    info["permissions"] = sorted(set(re.findall(r"uses-permission: name='([^']+)'", badging)))
    xmltree = run([str(AAPT2), "dump", "xmltree", "--file", "AndroidManifest.xml", apk])
    info["services"] = re.findall(r"E: service.*?(?=E: |\Z)", xmltree, re.S)
    info["components"] = {
        kind: len(re.findall(rf"E: {kind} ", xmltree)) for kind in ("activity", "service", "receiver", "provider")
    }
    bind_perms = re.findall(r'android:permission[^=]*="([^"]+)"', xmltree)
    info["component_bind_permissions"] = sorted(set(p for p in bind_perms if p.startswith("android.permission.BIND")))
    contents = run([str(AAPT2), "dump", "resources", apk])  # ensures apk is readable
    # asset listing via zip directory
    import zipfile
    with zipfile.ZipFile(apk) as z:
        info["assets"] = sorted(n for n in z.namelist() if n.startswith("assets/"))
    return info


def report(info: dict, markdown: bool):
    if markdown:
        print(f"### `{Path(info['apk']).name}`\n")
        print(f"| Field | Value |\n|---|---|")
        print(f"| Package | `{info.get('package','?')}` {info.get('versionName','')} |")
        print(f"| Label | {info.get('label','')} |")
        print(f"| APK SHA-256 | `{info['sha256']}` |")
        print(f"| Signing cert SHA-256 | `{', '.join(info['cert_sha256'])}` |")
        perms = "<br>".join(f"`{p}`" for p in info["permissions"]) or "*(none)*"
        print(f"| uses-permission | {perms} |")
        print(f"| Components | {info['components']} |")
        binds = "<br>".join(f"`{p}`" for p in info["component_bind_permissions"]) or "*(none)*"
        print(f"| Component bind permissions | {binds} |")
        assets = "<br>".join(f"`{a}`" for a in info["assets"]) or "*(none)*"
        print(f"| Assets | {assets} |\n")
    else:
        for key in ("apk", "package", "label", "sha256", "cert_sha256", "permissions",
                    "components", "component_bind_permissions", "assets"):
            print(f"{key}: {info.get(key)}")
        print()


def main():
    args = [a for a in sys.argv[1:] if not a.startswith("--")]
    markdown = "--markdown" in sys.argv
    for apk in args:
        report(audit(apk), markdown)


if __name__ == "__main__":
    main()
