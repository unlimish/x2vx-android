# x2vx for Android
Converting URL starts from `x.com` to `vxtwitter.com` via Sharing Intent to copy to clipboard without tracking links.

![x2vx-android](https://github.com/unlimish/x2vx-android/assets/14168376/43e35eaa-3657-41a2-9499-0ada2e9b633e)

 [<img src="https://raw.githubusercontent.com/enricocid/fdroid-custom-badges/main/badge_get-it-on-coming-soon.png"
    alt="Get it on F-Droid"
    height="80">](https://f-droid.org/packages/sh.unlimi.x2vx/)


## for iOS

  [<img src="https://github.com/unlimish/x2vx-android/assets/14168376/053ed578-32be-4bd1-ab52-73d6cf69c0b0"
    alt="Download on the Shortcuts"
    height="80">](https://www.icloud.com/shortcuts/fadff2730bd34ccba6f4d4b0c3761824)

## Diagram

```mermaid
graph LR
    A[MainActivity.onCreate] --> B[handleIntent]
    A --> C[setContentView]
    B --> D{Intent Action}
    

    F[Replace Domain]
    F --> G[Copy to Clipboard]
    G --> H[Show Toast]
    H --> I[Finish Activity]
    
    D -->|ACTION_SEND| J[Extract URL from Intent EXTRA_TEXT]
    J --> K{URL contains https://x.com?}
    K -->|Yes| F[Replace Domain]
    K -->|No| L[Show Invalid URL Toast]
    F --> G[Copy to Clipboard]
    G --> H[Show Toast]
    H --> I
    L --> I
```
