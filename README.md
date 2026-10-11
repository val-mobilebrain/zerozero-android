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

**Target SDK:** Android 36 (`compileSdk` / `targetSdk`).

Debug:

```bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

Signed release (local):

```bash
keytool -genkey -v -keystore release.keystore -alias zerozero -keyalg RSA -keysize 2048 -validity 10000
```

Add to `local.properties` (not committed):

```properties
RELEASE_STORE_FILE=release.keystore
RELEASE_STORE_PASSWORD=your_store_password
RELEASE_KEY_ALIAS=zerozero
RELEASE_KEY_PASSWORD=your_key_password
```

```bash
./gradlew assembleRelease
```

APK: `app/build/outputs/apk/release/app-release.apk`

## GitHub Actions

Workflow: `.github/workflows/android-release.yml` — runs on push to `main` and manual dispatch.

Repository secrets:

| Secret | Description |
|--------|-------------|
| `RELEASE_KEYSTORE_BASE64` | Base64 of `release.keystore` |
| `RELEASE_STORE_PASSWORD` | Keystore password |
| `RELEASE_KEY_ALIAS` | Key alias |
| `RELEASE_KEY_PASSWORD` | Key password |

Signed APK is uploaded as a workflow artifact.

## Notes

- Replace `YOUR_ONESIGNAL_APP_ID` before release; push init is skipped until a real ID is set.
- Icon source: `zerozero_ic.png` (generated into `mipmap-*`).
