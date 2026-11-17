#!/usr/bin/env python3
"""Collects cumulative download counts for x2vx from every source and writes
shields.io endpoint badges plus a history file.

Sources:
- GitHub Releases: download_count of every APK asset (this also covers
  Obtainium, which downloads from GitHub Releases)
- IzzyOnDroid: weekly per-app stats published at dlstats.izzyondroid.org
  (collected since December 2024)

Usage: download_stats.py <data dir>
The data dir holds the outputs and a cache of IzzyOnDroid weekly totals, so
finished weeks aren't downloaded again.
"""
import datetime
import json
import os
import re
import sys
import urllib.request

PKG = "sh.unlimi.x2vx"
IZZY_BASE = "https://dlstats.izzyondroid.org/iodstats/apk_downloads/"
# Re-read the newest weekly files each run in case they're still being filled.
IZZY_REFRESH_LATEST = 2


def fetch(url, headers=None):
    req = urllib.request.Request(url, headers={"User-Agent": "x2vx-download-stats", **(headers or {})})
    with urllib.request.urlopen(req, timeout=120) as resp:
        return resp.read()


def github_downloads(repo, token):
    headers = {"Accept": "application/vnd.github+json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    total, page = 0, 1
    while True:
        releases = json.loads(fetch(f"https://api.github.com/repos/{repo}/releases?per_page=100&page={page}", headers))
        if not releases:
            return total
        for release in releases:
            for asset in release["assets"]:
                if asset["name"].endswith(".apk"):
                    total += asset["download_count"]
        page += 1


def links(html, pattern):
    return [m for m in re.findall(r'href="([^"]+)"', html) if re.fullmatch(pattern, m)]


def izzy_downloads(cache):
    files = []
    for year in links(fetch(IZZY_BASE).decode(), r"\d{4}/"):
        for name in links(fetch(IZZY_BASE + year).decode(), r"iod_downloads_[\d-]+\.json"):
            files.append(year + name)
    files.sort()
    refresh = set(files[-IZZY_REFRESH_LATEST:])
    for path in files:
        if path in cache and path not in refresh:
            continue
        week = json.loads(fetch(IZZY_BASE + path))
        cache[path] = sum(day.get(PKG, {}).get("_total", 0) for day in week.values())
        print(f"IzzyOnDroid {path}: {cache[path]}")
    return sum(cache[path] for path in files)


def short(n):
    if n >= 1_000_000:
        return f"{n / 1_000_000:.1f}M".replace(".0M", "M")
    if n >= 1_000:
        return f"{n / 1_000:.1f}k".replace(".0k", "k")
    return str(n)


def badge(path, label, count, color):
    with open(path, "w") as f:
        json.dump({"schemaVersion": 1, "label": label, "message": short(count), "color": color}, f)
        f.write("\n")


def main():
    data_dir = sys.argv[1]
    os.makedirs(data_dir, exist_ok=True)
    cache_path = os.path.join(data_dir, "izzyondroid_weeks.json")
    cache = json.load(open(cache_path)) if os.path.exists(cache_path) else {}

    github = github_downloads(os.environ["GITHUB_REPOSITORY"], os.environ.get("GH_TOKEN"))
    izzy = izzy_downloads(cache)
    total = github + izzy
    today = datetime.datetime.now(datetime.timezone.utc).date().isoformat()
    print(f"GitHub: {github}, IzzyOnDroid: {izzy}, total: {total}")

    with open(cache_path, "w") as f:
        json.dump(cache, f, indent=1, sort_keys=True)
        f.write("\n")
    with open(os.path.join(data_dir, "summary.json"), "w") as f:
        json.dump({"date": today, "github": github, "izzyondroid": izzy, "total": total}, f, indent=1)
        f.write("\n")
    badge(os.path.join(data_dir, "downloads.json"), "downloads", total, "blue")
    badge(os.path.join(data_dir, "github.json"), "GitHub downloads", github, "blue")
    badge(os.path.join(data_dir, "izzyondroid.json"), "IzzyOnDroid downloads", izzy, "blue")

    # One row per day; a re-run on the same day replaces that day's row.
    history_path = os.path.join(data_dir, "history.csv")
    rows = []
    if os.path.exists(history_path):
        rows = [r for r in open(history_path).read().splitlines()[1:] if r and not r.startswith(today + ",")]
    rows.append(f"{today},{github},{izzy},{total}")
    with open(history_path, "w") as f:
        f.write("date,github,izzyondroid,total\n" + "\n".join(sorted(rows)) + "\n")


if __name__ == "__main__":
    main()
