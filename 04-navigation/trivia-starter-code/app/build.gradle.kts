plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.android.navigation"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.android.navigation"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.6")
}
