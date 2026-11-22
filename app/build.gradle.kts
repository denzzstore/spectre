plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "id.spectre.bugs"
    compileSdk = 33

    defaultConfig {
        applicationId = "id.spectre.bugs"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0"
        ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a") }
    }

    signingConfigs {
        create("rel") {
            storeFile = file(System.getenv("KS_PATH") ?: "spectre.keystore")
            storePassword = System.getenv("KS_PASS") ?: "spectrepass"
            keyAlias = System.getenv("KEY_ALIAS") ?: "spectre"
            keyPassword = System.getenv("KEY_PASS") ?: "spectrepass"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = if (file(System.getenv("KS_PATH") ?: "spectre.keystore").exists())
                signingConfigs.getByName("rel") else signingConfigs.getByName("debug")
        }
        debug { isMinifyEnabled = false }
    }

    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.8" }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    packaging {
        resources.excludes += setOf(
            "META-INF/*", "META-INF/DEPENDENCIES", "META-INF/LICENSE*",
            "META-INF/NOTICE*", "META-INF/versions/**"
        )
        jniLibs { useLegacyPackaging = true }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation(project(":nodejs-mobile"))
}
