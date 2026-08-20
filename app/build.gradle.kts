import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Release signing is driven by (in priority order) a gitignored `keystore.properties` at the repo
// root, or environment variables (CI secrets). If neither is present, release falls back to the
// debug key so CI/testing builds stay green. NOTHING secret is committed. See RELEASE_SIGNING.md.
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) keystorePropsFile.inputStream().use { load(it) }
}
fun signingValue(propKey: String, envKey: String): String? =
    keystoreProps.getProperty(propKey) ?: System.getenv(envKey)
val hasReleaseKeystore = signingValue("storeFile", "KEYSTORE_FILE") != null

android {
    namespace = "com.eight64zeros.clearstreak"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.eight64zeros.clearstreak"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64"))
        }
    }

    flavorDimensions += "distribution"
    productFlavors {
        create("core") {
            dimension = "distribution"
            // Default air-gapped local-first flavor: zero internet permission
            versionNameSuffix = "-core"
        }
        create("store") {
            dimension = "distribution"
            // Release/Store flavor: minimal network for IAP validation
            versionNameSuffix = "-store"
        }
    }

    signingConfigs {
        create("release") {
            if (hasReleaseKeystore) {
                storeFile = file(signingValue("storeFile", "KEYSTORE_FILE")!!)
                storePassword = signingValue("storePassword", "KEYSTORE_PASSWORD")
                keyAlias = signingValue("keyAlias", "KEY_ALIAS")
                keyPassword = signingValue("keyPassword", "KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Real release key when provided (keystore.properties / CI secrets); debug fallback
            // keeps CI + local testing builds green without secrets. Not Play-uploadable until signed.
            signingConfig = if (hasReleaseKeystore)
                signingConfigs.getByName("release")
            else
                signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
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
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    // Hardware Security & Biometrics
    implementation(libs.androidx.biometric)

    // SQLCipher AES-256 Encrypted Database & SQLite
    implementation(libs.sqlcipher)
    implementation(libs.androidx.sqlite)

    // Jetpack Glance (Widgets)
    implementation(libs.androidx.glance)
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Google Play Billing — store flavor only; core stays air-gapped (no INTERNET, no billing)
    "storeImplementation"(libs.billing)

    // Google Play In-App Review — store flavor only
    "storeImplementation"(libs.play.review)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
