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
| `KEYSTORE_FILE` | Path to the keystore file (relative to project root, e.g. `release.keystore`) |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key password |

Example `local.properties` entries:

```properties
KEYSTORE_FILE=release.keystore
KEYSTORE_PASSWORD=123456
KEY_ALIAS=zerozero
KEY_PASSWORD=123456
```

When all four values are set and the keystore file exists, `assembleRelease` produces a signed APK.

## CI (GitHub Actions)

Workflow: [`.github/workflows/android-release.yml`](.github/workflows/android-release.yml)

Triggers on push to `main` and manual **Run workflow**.

Configure these **repository secrets** (Settings → Secrets and variables → Actions):

| Secret | Description |
| --- | --- |
| `KEYSTORE_BASE64` | Full contents of `release.keystore.base64.txt` (one line) |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias (`zerozero`) |
| `KEY_PASSWORD` | Key password (same as keystore for PKCS12) |

The workflow decodes the keystore to `release.keystore` in the repo root and runs `./gradlew bundleRelease` (target/compile SDK **36**). The signed AAB is uploaded as a workflow artifact (`zerozero-release-aab`).

Local signed AAB:

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

## Notes

- Replace `YOUR_ONESIGNAL_APP_ID` before release; push init is skipped until a real ID is set.
- Icon source: `zerozero_ic.png` (generated into `mipmap-*`).
