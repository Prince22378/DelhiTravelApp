plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
//    alias(libs.plugins.kotlin.kapt)
}

android {
    namespace = "com.example.delhitravelapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.delhitravelapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.core.ktx.v1120)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Retrofit & Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Compose Tooling
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.material.icons.extended)

    //http3
    implementation(libs.logging.interceptor)

    //room dependencies
    implementation(libs.androidx.room.runtime)
    //noinspection KaptUsageInsteadOfKsp
//    kapt("androidx.room:room-compiler:2.6.1")
    implementation(libs.androidx.room.ktx)

    kapt("androidx.room:room-compiler:2.7.1")
    // Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose.v261)

    // Compose BOM + UI
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Retrofit & Gson
    implementation(libs.retrofit)
    implementation(libs.retrofit2.converter.gson)

    // OkHttp Logging
    implementation(libs.logging.interceptor)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.material)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug tools
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // For FlowRow (layout)
    implementation(libs.androidx.foundation.layout)
    // Core Compos

    // Material 3
    implementation(libs.androidx.material3)

    // Lazy grid is in foundation (no extra artifact)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material3)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.ui)
    implementation(libs.material3)
//    implementation(libs.androidx.compose.ui)
//    implementation(libs.androidx.compose.ui.text)
//    implementation(libs.androidx.compose.foundation)
//    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.core.ktx.v1131)
    implementation(libs.androidx.activity.compose.v190)
    implementation(platform(libs.androidx.compose.bom.v20240500))
    // Core UI
    implementation(libs.ui)
    // Foundation (Layouts, scrolling, etc.)
    implementation(libs.androidx.foundation)
    // Material 3
    implementation(libs.material3)
    // Icon packs (for Icons.Default.ArrowDropDown, etc.)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)
    // Preview tooling
    implementation(libs.ui.tooling.preview)
    // ViewModel + Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // Navigation
    implementation(libs.androidx.navigation.compose)
    // Foundation (includes scrolling, layouts, etc.)
    implementation(libs.foundation)
// Material 3 core
    implementation(libs.androidx.compose.material3.material3)
// Material Icons (for Icons.Default.ArrowDropDown)
    implementation(libs.material.icons.core)
    implementation(libs.material.icons.extended)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.material)
    implementation(libs.androidx.room.common)
    implementation(platform(libs.compose.bom.v20240500))
    implementation(libs.androidx.foundation.layout)

    // For press animations:
    implementation(libs.androidx.animation.core)
// For SearchBar and icons:
    implementation(libs.androidx.compose.material3.material32)
    implementation(libs.androidx.material.icons.extended.v143)
    implementation(libs.androidx.datastore.preferences)


}