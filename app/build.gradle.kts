plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-parcelize") // Add this line
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.sevalk"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sevalk"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file(project.property("MYAPP_RELEASE_STORE_FILE") as String)
            storePassword = project.property("MYAPP_RELEASE_STORE_PASSWORD") as String
            keyAlias = project.property("MYAPP_RELEASE_KEY_ALIAS") as String
            keyPassword = project.property("MYAPP_RELEASE_KEY_PASSWORD") as String
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            // This creates BuildConfig.DEBUG = true
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

    }

    // Add packaging options to handle duplicate files from email libraries
    packaging {
        resources {
            excludes += listOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/NOTICE.md",
                "META-INF/DEPENDENCIES",
                "META-INF/ASL2.0",
                "META-INF/LICENSE",
                "META-INF/NOTICE"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        // Enable proper thread handling for network operations
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
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
    implementation("com.google.maps.android:maps-compose:2.11.4")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation(libs.play.services.maps)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.crashlytics.buildtools)

    // Splash Screen API
    implementation("androidx.core:core-splashscreen:1.0.0")


    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Core library desugaring (for proper thread handling)
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")

    // Email sending dependencies
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")


    // Change status bar color
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(platform("androidx.compose:compose-bom:2023.06.01"))
    implementation("com.jakewharton.timber:timber:5.0.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation ("com.google.firebase:firebase-firestore-ktx:24.9.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Supabase
    implementation("io.github.jan-tennert.supabase:storage-kt:2.0.0")
    implementation("io.github.jan-tennert.supabase:realtime-kt:2.0.0")
    implementation("io.ktor:ktor-client-android:2.3.7")
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-utils:2.3.7")

    // Coil for AsyncImage
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.0")

    // Stripe Android SDK
    implementation("com.stripe:stripe-android:21.21.0")
    implementation("com.stripe:financial-connections:21.21.0")

    // Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
}