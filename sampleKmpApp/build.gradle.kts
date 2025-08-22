import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.native.cocoapods)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    cocoapods {
        ios.deploymentTarget = libs.versions.build.ios.target.deployment.get()
        noPodspec()
        pod("Google-Mobile-Ads-SDK") {
            moduleName = "GoogleMobileAds"
            version = libs.versions.cocoapods.admob.get()
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
        pod("GoogleUserMessagingPlatform") {
            moduleName = "UserMessagingPlatform"
            version = libs.versions.cocoapods.ump.get()
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "sampleKmpApp"
            isStatic = true
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation("apero-adkit:common-ads-sdk-admob:1.0.1"){
                isChanging = true
            }
        }
    }
}
configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}
android {
    namespace = "com.apero.kmm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.apero.kmm"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

