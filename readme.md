# x2vx for Android
Share an X (x.com), TikTok, Bluesky or Reddit link to x2vx, and it copies an embed-friendly link to your clipboard, with tracking parameters removed.

![x2vx-android](https://github.com/unlimish/x2vx-android/assets/14168376/43e35eaa-3657-41a2-9499-0ada2e9b633e)

## Features
- Converts links shared from any app via the Android share sheet
- Removes query parameters (e.g. `?s=46`, `?utm_source=...`) and fragments
- Settings screen (open x2vx from the app drawer):
  - **Proxy for x.com links:** `vxtwitter.com` (default), `fixvx.com`, `fxtwitter.com`, `fixupx.com`
  - **Translate posts:** with `fxtwitter.com` / `fixupx.com`, append a target language to post links (e.g. `.../status/123/ja`)
  - **Open the share menu after converting:** send the converted link straight to another app (Share → x2vx → Telegram)

| Platform | Converted to |
|---|---|
| X (x.com) | selected proxy (see above) |
| TikTok | `tnktok.com` |
| Bluesky | `fxbsky.app` |
| Reddit | `vxreddit.com` |

## How to use
1. Tap **Share** on a post in X, TikTok, Bluesky or Reddit
2. Choose **x2vx**
3. The converted link is copied to the clipboard. Paste it anywhere.

x2vx doesn't run in the background or watch the clipboard: Android doesn't let apps read the clipboard in the background, or change links shared directly to other apps. Always share to x2vx first.

## Available on

> ⚠️ While you can download the APKs from GitHub directly, you should preferably use an F-Droid client or Obtainium so the app stays up-to-date on your device.
> <br>APKをGitHubで直接ダウンロードできますが、デバイス上のアプリを最新の状態に保つために、F-Droid クライアントまたは Obtainium からインストールすることをお勧めします。

[<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png"
    alt="Get it on IzzyOnDroid"
    height="80">](https://apt.izzysoft.de/fdroid/index/apk/sh.unlimi.x2vx)
[<img src="https://raw.githubusercontent.com/ImranR98/Obtainium/main/assets/graphics/badge_obtainium.png"
    alt="Get it on Obtainium"
    height="80">](https://apps.obtainium.imranr.dev/redirect?r=obtainium://add/https://github.com/unlimish/x2vx-android)
[<img src="https://github.com/unlimish/x2vx-android/assets/14168376/c4f11a6e-730b-4c4d-b676-a11a1ed42831"
    alt="Get it on GitHub"
    height="80">](https://github.com/unlimish/x2vx-android/releases/)

- [IzzyOnDroid F-Droid Repository](https://apt.izzysoft.de/fdroid/index/apk/sh.unlimi.x2vx) `https://apt.izzysoft.de/fdroid/repo`
- [Obtainium](https://github.com/ImranR98/Obtainium): installs and updates the app straight from GitHub Releases. Add `https://github.com/unlimish/x2vx-android` as the app source, or tap the badge above on your phone.

[![IzzyOnDroid](https://img.shields.io/endpoint?url=https://apt.izzysoft.de/fdroid/api/v1/shield/sh.unlimi.x2vx&style=for-the-badge)](https://apt.izzysoft.de/fdroid/index/apk/sh.unlimi.x2vx)
![GitHub Release](https://img.shields.io/github/v/release/unlimish/x2vx-android?style=for-the-badge)
[![Downloads](https://img.shields.io/endpoint?url=https://raw.githubusercontent.com/unlimish/x2vx-android/stats/downloads.json&style=for-the-badge)](https://github.com/unlimish/x2vx-android/blob/stats/history.csv)
![MIT](https://img.shields.io/github/license/unlimish/x2vx-android.svg?style=for-the-badge)

The downloads badge adds up GitHub Releases (including Obtainium) and IzzyOnDroid (counted since December 2024). It's updated daily; see the [history](https://github.com/unlimish/x2vx-android/blob/stats/history.csv) for the numbers per source.

## for iOS

  [<img src="https://github.com/unlimish/x2vx-android/assets/14168376/053ed578-32be-4bd1-ab52-73d6cf69c0b0"
    alt="Download on the Shortcuts"
    height="80">](https://www.icloud.com/shortcuts/fadff2730bd34ccba6f4d4b0c3761824)

## Flow
![untitled](https://github.com/unlimish/x2vx-android/assets/14168376/cf17c79e-14f1-4895-a4a1-8f7ef933ca89)

## Thanks
The proxies are run by these projects:
[vxTwitter](https://github.com/dylanpdx/BetterTwitFix),
[FxEmbed](https://github.com/FxEmbed/FxEmbed) (fxtwitter / fixupx / fxbsky),
[fxTikTok](https://github.com/okdargy/fxTikTok),
[vxReddit](https://github.com/dylanpdx/vxReddit)
