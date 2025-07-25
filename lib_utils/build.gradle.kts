plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    kotlin("kapt")
}

android {
    namespace = "com.aiso.qfast.utils"
    compileSdk = 35

    defaultConfig {
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
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.glide)
    implementation(libs.glide.transformations)
    //noinspection KaptUsageInsteadOfKsp
    kapt(libs.compiler)
//    api(libs.toaster)
    implementation(libs.utilcodex)
    implementation(libs.timber)
    implementation(libs.commons.math3)
    implementation(libs.converter.gson)
    implementation(libs.fastjson)
    implementation (libs.pictureselector)
    api(libs.ucrop)
    api(libs.compress)
    api(libs.mmkv)

    implementation(project(":lib_base"))
}