#!/usr/bin/env bash
# Prepares APKs for the smoke test in ./smoke:
#   smoke/new.apk  the release APK built by this run (signed with a throwaway
#                  key if CI had no signing secrets, e.g. pull requests from forks)
#   smoke/old.apk  the latest GitHub release, only if it has the same signer,
#                  so the test can check that updating keeps the user's settings
set -euo pipefail

BUILD_TOOLS="$(ls -d "$ANDROID_HOME"/build-tools/*/ | sort -V | tail -n 1)"
APKSIGNER="${BUILD_TOOLS}apksigner"
NEW_SRC="$(ls apk/release/*.apk | head -n 1)"
mkdir -p smoke

if "$APKSIGNER" verify "$NEW_SRC" > /dev/null 2>&1; then
  cp "$NEW_SRC" smoke/new.apk
else
  echo "Release APK is unsigned; signing it with a throwaway key for testing."
  keytool -genkeypair -keystore smoke/test.jks -storepass android -keypass android \
    -alias test -keyalg RSA -keysize 2048 -validity 1 -dname CN=test
  "$APKSIGNER" sign --ks smoke/test.jks --ks-pass pass:android --out smoke/new.apk "$NEW_SRC"
fi

cert() { "$APKSIGNER" verify --print-certs "$1" | grep -m 1 'SHA-256'; }

url="$(curl -fsSL -H "Authorization: Bearer $GH_TOKEN" \
  "https://api.github.com/repos/$GITHUB_REPOSITORY/releases/latest" \
  | python3 -c 'import sys, json; a = [x["browser_download_url"] for x in json.load(sys.stdin).get("assets", []) if x["name"].endswith("_release.apk")]; print(a[0] if a else "")' \
  || true)"

if [ -n "$url" ] && curl -fsSL -o smoke/old.apk "$url"; then
  if [ "$(cert smoke/old.apk)" = "$(cert smoke/new.apk)" ]; then
    echo "Will test updating from $url"
  else
    echo "Latest release has a different signer; skipping the update test."
    rm -f smoke/old.apk
  fi
else
  echo "No previous release APK; skipping the update test."
  rm -f smoke/old.apk
fi
