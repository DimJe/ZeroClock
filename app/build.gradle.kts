import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension

plugins {
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.plugin)
    alias(libs.plugins.ksp)
}

// 검증할 때만 릴리스 APK에 디버그 서명을 사용하며 운영 오류 수집은 끕니다.
val releaseVerification = providers.gradleProperty("releaseVerification").orNull == "true"

android {
    namespace = "com.dimje.zeroclock"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.dimje.zeroclock"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            manifestPlaceholders["crashlyticsCollectionEnabled"] = "false"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            manifestPlaceholders["crashlyticsCollectionEnabled"] = (!releaseVerification).toString()
            configure<CrashlyticsExtension> {
                mappingFileUploadEnabled = !releaseVerification
            }
            if (releaseVerification) {
                signingConfig = signingConfigs.getByName("debug")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)

    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.core)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.room.runtime)
    androidTestImplementation(libs.retrofit.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.compose.navigation)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.compose)
}
