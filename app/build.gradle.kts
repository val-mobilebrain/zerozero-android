import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

fun readSigningValue(key: String): String? {
    System.getenv(key)?.takeIf { it.isNotBlank() }?.let { return it }
    val localProperties = rootProject.file("local.properties")
    if (localProperties.exists()) {
        val props = Properties()
        localProperties.inputStream().use { props.load(it) }
        props.getProperty(key)?.takeIf { it.isNotBlank() }?.let { return it }
    }
    return null
}

val releaseStoreFilePath = readSigningValue("RELEASE_STORE_FILE")
val releaseStorePassword = readSigningValue("RELEASE_STORE_PASSWORD")
val releaseKeyAlias = readSigningValue("RELEASE_KEY_ALIAS")
val releaseKeyPassword = readSigningValue("RELEASE_KEY_PASSWORD")

val hasReleaseSigning =
    !releaseStoreFilePath.isNullOrBlank() &&
        !releaseStorePassword.isNullOrBlank() &&
        !releaseKeyAlias.isNullOrBlank() &&
        !releaseKeyPassword.isNullOrBlank() &&
        rootProject.file(releaseStoreFilePath).exists()

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
