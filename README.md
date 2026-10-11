# ZeroZero (Android)

Native Android app with WebView for [zerozero.pt](https://www.zerozero.pt/).

- **Package:** `com.zerozerosportnewsappmobzerozero`
- **Splash:** 11 seconds, then main screen
- **Push:** OneSignal SDK 5.x
- **Target SDK:** 36 (Android 16)

## Setup

1. Open the project in Android Studio (Ladybug or newer).
2. **OneSignal:** In `app/src/main/res/values/strings.xml`, set `onesignal_app_id` to your App ID from OneSignal → Settings → Keys & IDs.
3. **Firebase (push):** Create a Firebase project for this package name, download `google-services.json` into the `app/` folder. The Google Services plugin is applied automatically when that file exists (this file is gitignored—do not commit it).
4. In OneSignal dashboard, add your Firebase Server Key / FCM credentials for Android.

## Build

Debug (local):

```bash
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

Release (signed, when signing credentials are configured):

```bash
./gradlew assembleRelease
```

APK: `app/build/outputs/apk/release/app-release.apk` (signed) or `app-release-unsigned.apk` if signing credentials are missing

### Release signing (local)

Generate a upload/release keystore once (keep the file and passwords safe; never commit the keystore):

```bash
keytool -genkeypair -v \
  -keystore release.keystore \
  -alias zerozero \
  -keyalg RSA -keysize 2048 -validity 10000
```

Provide credentials via environment variables or `local.properties` (gitignored):

| Property / env var | Description |
| --- | --- |
| `RELEASE_STORE_FILE` | Path to the keystore file (relative to project root, e.g. `release.keystore`) |
| `RELEASE_STORE_PASSWORD` | Keystore password |
| `RELEASE_KEY_ALIAS` | Key alias |
| `RELEASE_KEY_PASSWORD` | Key password |

Example `local.properties` entries:

```properties
RELEASE_STORE_FILE=release.keystore
RELEASE_STORE_PASSWORD=your-store-password
RELEASE_KEY_ALIAS=zerozero
RELEASE_KEY_PASSWORD=your-key-password
```

When all four values are set and the keystore file exists, `assembleRelease` produces a signed APK. Without them, the release APK is built unsigned (suitable for CI smoke builds only).

## CI (GitHub Actions)

Workflow: [`.github/workflows/android-release.yml`](.github/workflows/android-release.yml)

Triggers on push to `main` and manual **Run workflow**.

Configure these **repository secrets** (Settings → Secrets and variables → Actions):

| Secret | Description |
| --- | --- |
| `RELEASE_KEYSTORE_BASE64` | Base64-encoded release keystore file (`base64 -i release.keystore \| pbcopy` on macOS) |
| `RELEASE_STORE_PASSWORD` | Keystore password |
| `RELEASE_KEY_ALIAS` | Key alias |
| `RELEASE_KEY_PASSWORD` | Key password |

The workflow decodes the keystore to `release.keystore` in the repo root and runs `./gradlew assembleRelease`. The signed APK is uploaded as a workflow artifact (`zerozero-release-apk`).

## Notes

- Replace `YOUR_ONESIGNAL_APP_ID` before release; push init is skipped until a real ID is set.
- Icon source: `zerozero_ic.png` (generated into `mipmap-*`).
