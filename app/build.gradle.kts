import org.jetbrains.kotlin.gradle.plugin.extraProperties

plugins {

    id("com.android.application")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")

}

android {
    namespace = "android.tvz.hr.tic_tac_toe_online"
    compileSdk = 34

    defaultConfig {
        applicationId = "android.tvz.hr.tic_tac_toe_online"
        minSdk = 30
        targetSdk = 34
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
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    kotlin {
        sourceSets {
            main {
                java {
                    kotlin.srcDir("build/generated/source/navigation-args")
                }
            }
        }
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.datastore.core.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.moshi)
    //implementation (libs.androidx.datastore.preferences)
    implementation (libs.androidx.datastore.preferences.v100)
    implementation (libs.androidx.lifecycle.livedata.ktx)



}