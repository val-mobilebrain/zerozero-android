# ZeroZero (Android)

Native Android app with WebView for [zerozero.pt](https://www.zerozero.pt/).

- **Package:** `com.zerozerosportnewsappmobzerozero`
- **Splash:** 11 seconds, then main screen
- **Push:** OneSignal SDK 5.x

## Setup

1. Open the project in Android Studio (Ladybug or newer).
2. **OneSignal:** In `app/src/main/res/values/strings.xml`, set `onesignal_app_id` to your App ID from OneSignal → Settings → Keys & IDs.
3. **Firebase (push):** Create a Firebase project for this package name, download `google-services.json` into the `app/` folder. The Google Services plugin is applied automatically when that file exists.
4. In OneSignal dashboard, add your Firebase Server Key / FCM credentials for Android.

## Build

```bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Notes

- Replace `YOUR_ONESIGNAL_APP_ID` before release; push init is skipped until a real ID is set.
- Icon source: `zerozero_ic.png` (generated into `mipmap-*`).
