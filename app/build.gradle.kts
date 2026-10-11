import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

fun readSigningValue(vararg keys: String): String? {
    for (key in keys) {
        System.getenv(key)?.takeIf { it.isNotBlank() }?.let { return it }
        val localProperties = rootProject.file("local.properties")
        if (localProperties.exists()) {
            val props = Properties()
            localProperties.inputStream().use { props.load(it) }
            props.getProperty(key)?.takeIf { it.isNotBlank() }?.let { return it }
        }
    }
    return null
}

val releaseStoreFilePath = readSigningValue("RELEASE_STORE_FILE", "KEYSTORE_FILE")
val releaseStorePassword = readSigningValue("KEYSTORE_PASSWORD", "RELEASE_STORE_PASSWORD")
val releaseKeyAlias = readSigningValue("KEY_ALIAS", "RELEASE_KEY_ALIAS")
val releaseKeyPassword = readSigningValue("KEY_PASSWORD", "RELEASE_KEY_PASSWORD")

val hasReleaseSigning =
    !releaseStoreFilePath.isNullOrBlank() &&
        !releaseStorePassword.isNullOrBlank() &&
        !releaseKeyAlias.isNullOrBlank() &&
        !releaseKeyPassword.isNullOrBlank() &&
        rootProject.file(releaseStoreFilePath).exists()

gradle.taskGraph.whenReady {
    if (hasReleaseSigning.not() &&
        allTasks.any { it.path == ":app:assembleRelease" || it.path == ":app:packageRelease" }
    ) {
        error(
            "Release signing is not configured. Set KEYSTORE_FILE, KEYSTORE_PASSWORD, " +
                "KEY_ALIAS, and KEY_PASSWORD (env or local.properties).",
        )
    }
}

android {
    namespace = "com.zerozerosportnewsappmobzerozero"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.zerozerosportnewsappmobzerozero"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        if (hasReleaseSigning) {
            create("release") {
                storeFile = rootProject.file(releaseStoreFilePath!!)
                storePassword = releaseStorePassword
                keyAlias = releaseKeyAlias
                keyPassword = releaseKeyPassword
                enableV1Signing = true
                enableV2Signing = true
                enableV3Signing = true
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            if (hasReleaseSigning) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.webkit:webkit:1.12.1")
    implementation("com.onesignal:OneSignal:[5.6.1, 5.9.99]")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
}

// Apply Firebase / Google Services when google-services.json is present (required for push).
if (file("google-services.json").exists()) {
    apply(plugin = "com.google.gms.google-services")
}
