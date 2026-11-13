plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "pl.motogarage.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "pl.motogarage.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "0.3"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}
