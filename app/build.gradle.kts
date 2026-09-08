plugins {
    alias(libs.plugins.android.application)
    // alias(libs.plugins.kotlin.android)
    // id("org.jetbrains.kotlin.kapt") // this is being applied directly instead of as an alias
}

android {
    namespace = "com.trios2025dej.itunespodcast2026"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.trios2025dej.itunespodcast2026"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // Glide
    implementation(libs.glide)
    // kapt(libs.glide.compiler)

    // Gson
    implementation(libs.gson)

    //RecyclerView
    implementation(libs.recyclerview)

    // Lifecycle ViewModel
    implementation(libs.lifecycle.viewmodel)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}