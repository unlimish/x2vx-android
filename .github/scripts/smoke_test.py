#!/usr/bin/env python3
"""End-to-end smoke test for x2vx, run on an emulator against the release APK.

1. Install the previous release (if available), turn on fixupx.com, translation
   and "open the share menu" through the UI.
2. Update to the new APK and check that those settings survived (R8 must not
   rename anything stored in SharedPreferences).
3. Share text containing an x.com link to x2vx and check that the share menu
   opens with the converted link.
4. Fail if the app crashed at any point.

Screenshots and the last UI dump are saved to ./smoke for debugging.
"""
import os
import re
import shlex
import subprocess
import sys
import time
import xml.etree.ElementTree as ET

PKG = "sh.unlimi.x2vx"
ACTIVITY = f"{PKG}/.MainActivity"
SAMPLE_TEXT = "Check this out! https://x.com/unlimish/status/1773686985728962615?s=46"
EXPECTED_URL = "https://fixupx.com/unlimish/status/1773686985728962615/en"
SWITCHES = ("Translate posts", "Open the share menu")
OUT = "smoke"


def run(*args, check=True):
    print("+", " ".join(args), flush=True)
    result = subprocess.run(args, capture_output=True, text=True)
    if check and result.returncode != 0:
        raise RuntimeError(f"{' '.join(args)} failed:\n{result.stdout}{result.stderr}")
    return result.stdout


def adb(*args, check=True):
    return run("adb", *args, check=check)


def screenshot(name):
    data = subprocess.run(["adb", "exec-out", "screencap", "-p"], capture_output=True).stdout
    with open(os.path.join(OUT, name), "wb") as f:
        f.write(data)


def fail(message):
    print(f"\nFAIL: {message}", flush=True)
    try:
        screenshot("failure.png")
        with open(os.path.join(OUT, "failure.xml"), "w") as f:
            f.write(adb("shell", "cat", "/sdcard/ui.xml", check=False))
    except Exception as e:  # keep the original failure visible
        print(f"(could not save debug output: {e})")
    print(adb("logcat", "-d", "-b", "crash", check=False))
    sys.exit(1)


def dump():
    for _ in range(5):
        if "dumped" in adb("shell", "uiautomator", "dump", "/sdcard/ui.xml", check=False):
            return ET.fromstring(adb("shell", "cat", "/sdcard/ui.xml"))
        time.sleep(1)
    fail("uiautomator dump failed")


def find(root, text, contains=False):
    for node in root.iter("node"):
        value = node.get("text", "")
        if (text in value) if contains else (value == text):
            return node
    return None


def wait_for(text, contains=False, timeout=20):
    deadline = time.time() + timeout
    while time.time() < deadline:
        root = dump()
        node = find(root, text, contains)
        if node is not None:
            return root, node
        time.sleep(1)
    fail(f"timed out waiting for {text!r} on screen")


def tap(node):
    x1, y1, x2, y2 = map(int, re.findall(r"\d+", node.get("bounds")))
    adb("shell", "input", "tap", str((x1 + x2) // 2), str((y1 + y2) // 2))
    time.sleep(1)


def is_checked(node):
    return node is not None and node.get("checked") == "true"


def launch():
    adb("shell", "am", "force-stop", PKG)
    adb("shell", "am", "start", "-W", "-n", ACTIVITY)
    return wait_for("fixupx.com")


def configure():
    _, provider = launch()
    tap(provider)
    for label in SWITCHES:
        node = find(dump(), label)
        if node is None:
            fail(f"switch {label!r} not found")
        if not is_checked(node):
            tap(node)
    verify_settings(dump(), "after changing settings")


def verify_settings(root, when):
    if not is_checked(find(root, "fixupx.com")):
        fail(f"fixupx.com is not selected {when}")
    for label in SWITCHES:
        if not is_checked(find(root, label)):
            fail(f"{label!r} is not on {when}")
    if find(root, EXPECTED_URL, contains=True) is None:
        fail(f"preview doesn't show {EXPECTED_URL} {when}")
    print(f"OK: settings are correct {when}")


def version_code():
    out = adb("shell", "dumpsys", "package", PKG)
    match = re.search(r"versionCode=(\d+)", out)
    return match.group(1) if match else "?"


def check_no_crash():
    log = adb("logcat", "-d", "-b", "crash", check=False)
    if PKG in log:
        fail("the app crashed")


def main():
    os.makedirs(OUT, exist_ok=True)
    old_apk = os.path.join(OUT, "old.apk")
    new_apk = os.path.join(OUT, "new.apk")

    # The "copied to clipboard" pop-up (Android 13+) can cover the share menu.
    adb("shell", "device_config", "put", "systemui", "clipboard_overlay_enabled", "false", check=False)
    adb("logcat", "-b", "crash", "-c", check=False)
    adb("uninstall", PKG, check=False)

    if os.path.exists(old_apk):
        adb("install", old_apk)
        print(f"Installed previous release, versionCode {version_code()}")
        configure()
        adb("install", "-r", new_apk)
        print(f"Updated to versionCode {version_code()}")
        root, _ = launch()
        verify_settings(root, "after updating")
    else:
        adb("install", new_apk)
        print(f"Installed versionCode {version_code()} (no update test)")
        configure()
        root, _ = launch()
        verify_settings(root, "after restarting")
    screenshot("settings.png")
    check_no_crash()

    adb("shell", "am", "force-stop", PKG)
    adb("shell", "input", "keyevent", "KEYCODE_HOME")
    adb("shell", "am", "start", "-W", "-a", "android.intent.action.SEND", "-t", "text/plain",
        "--es", "android.intent.extra.TEXT", shlex.quote(SAMPLE_TEXT), "-n", ACTIVITY)
    wait_for(EXPECTED_URL, contains=True)
    print(f"OK: the share menu opened with {EXPECTED_URL}")
    screenshot("share.png")
    adb("shell", "input", "keyevent", "KEYCODE_BACK")
    check_no_crash()

    print("\nPASS")


if __name__ == "__main__":
    main()
