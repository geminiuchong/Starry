plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.group3_starry"
    compileSdk = 35 // Ensure this matches your project requirements.

    defaultConfig {
        applicationId = "com.example.group3_starry"
        minSdk = 25 // Minimum SDK version your app supports.
        targetSdk = 35 // Ensure it matches compileSdk for consistency.
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true // Ensure view binding is enabled for efficient UI coding.
    }
}

dependencies {
    // Firebase Authentication
    implementation(libs.firebase.auth.ktx)

    // OkHttp Logging Interceptor for debugging HTTP requests
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Retrofit Gson Converter for parsing JSON
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Kotlin Standard Library
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")

    // OkHttp and Logging Interceptor
    implementation(libs.okhttp)
    implementation(libs.okhttpLoggingInterceptor) // Handles HTTP request logging.

    // Coroutines for background tasks
    implementation(libs.coroutinesAndroid)

    // AndroidX Core and UI libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // AndroidX Lifecycle and Navigation components
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Unit testing
    testImplementation(libs.junit)

    // Instrumentation testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.libraries.places:places:3.1.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // Add Kotlin Script Runtime for solving the script runtime issue
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.8.0")
}

