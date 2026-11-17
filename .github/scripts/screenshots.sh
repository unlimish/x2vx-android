#!/usr/bin/env bash
# Takes store screenshots on a running emulator.
# Writes fastlane/metadata/android/<locale>/images/phoneScreenshots/{1,2}.png
set -euo pipefail

PKG=sh.unlimi.x2vx
ACTIVITY="$PKG/.MainActivity"
META=fastlane/metadata/android
SAMPLE_TEXT='Check this out! https://x.com/unlimish/status/1773686985728962615?s=46'

APK=$(ls app/build/outputs/apk/debug/*.apk | head -n 1)
adb install -r "$APK"

# Clean status bar (fixed clock, full battery, no notifications)
adb shell settings put global sysui_demo_allowed 1
demo() { adb shell am broadcast -a com.android.systemui.demo -e command "$@" > /dev/null; }
demo enter
demo clock -e hhmm 1200
demo battery -e level 100 -e plugged false
demo network -e wifi show -e level 4
demo network -e mobile hide
demo notifications -e visible false

set_locale() {
  adb shell cmd locale set-app-locales "$PKG" --locales "$1"
}

# provider, translate language
set_prefs() {
  local tmp
  tmp=$(mktemp)
  cat > "$tmp" <<EOF
<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
<map>
    <string name="provider">$1</string>
    <boolean name="translate_enabled" value="true" />
    <string name="translate_language">$2</string>
    <boolean name="share_after_convert" value="true" />
</map>
EOF
  adb shell am force-stop "$PKG"
  adb shell "run-as $PKG sh -c 'mkdir -p shared_prefs && cat > shared_prefs/settings.xml'" < "$tmp"
  rm -f "$tmp"
}

shot() {
  mkdir -p "$(dirname "$1")"
  adb exec-out screencap -p > "$1"
  echo "saved $1"
}

take() { # locale-dir, app-locale, translate-language
  local dir="$META/$1/images/phoneScreenshots"
  set_locale "$2"
  set_prefs FIXUPX "$3"

  # 1: settings screen
  adb shell am start -W -n "$ACTIVITY" > /dev/null
  sleep 3
  shot "$dir/1.png"

  # 2: share a link to x2vx; the share menu opens with the converted link
  adb shell am force-stop "$PKG"
  adb shell input keyevent KEYCODE_HOME
  sleep 1
  adb shell am start -W -a android.intent.action.SEND -t text/plain \
    --es android.intent.extra.TEXT "\"$SAMPLE_TEXT\"" -n "$ACTIVITY" > /dev/null
  sleep 4
  shot "$dir/2.png"
  adb shell input keyevent KEYCODE_BACK
  adb shell input keyevent KEYCODE_HOME
}

take en-US en-US ja
take ja-JP ja-JP en
