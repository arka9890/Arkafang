// app/build.gradle.kts
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20"
}

// Read signing config from gradle.properties (set by CI from GitHub Secrets).
// Files: keystore.jks, keystore.password, key.password, keystore.alias
val signingStoreFile = (findProperty("signing.storeFile") as String?) ?: "keystore.jks"
val signingStorePassword = (findProperty("signing.storePassword") as String?) ?: ""
val signingKeyAlias = (findProperty("signing.keyAlias") as String?) ?: "openfang"
val signingKeyPassword = (findProperty("signing.keyPassword") as String?) ?: ""

android {
    namespace = "com.openfang.mobile"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.openfang.mobile"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "0.1.0"
        ndk {
            abiFilters += "arm64-v8a"
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (signingStorePassword.isNotEmpty() && file(signingStoreFile).exists()) {
            create("release") {
                storeFile = file(signingStoreFile)
                storePassword = signingStorePassword
                keyAlias = signingKeyAlias
                keyPassword = signingKeyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Use the signing config if available; otherwise leave unsigned
            // (CI always sets it; local builds without secrets stay unsigned).
            val cfg = signingConfigs.findByName("release")
            if (cfg != null) signingConfig = cfg
        }
        debug {
            // Debug uses the default debug keystore (auto-generated)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    // Core modules
    implementation(project(":core:proot"))
    implementation(project(":core:openfang-api"))

    // AndroidX + Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.text)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp("com.google.dagger:hilt-android-compiler:${libs.versions.hilt.get()}")

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.client.websockets)
    implementation(libs.kotlinx.serialization.json)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Unpack
    implementation(libs.apache.commons.compress)
    implementation(libs.xz.java)

    // Security
    implementation(libs.androidx.security.crypto)

    // Test
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.ktor.server.test.host)
    testImplementation("com.google.dagger:hilt-android-testing:${libs.versions.hilt.get()}")
    kspTest("com.google.dagger:hilt-compiler:${libs.versions.hilt.get()}")
    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso.core)
}
