plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.wyz.app"
    compileSdk = 35

    signingConfigs {
        create("release") {
            storeFile = file("release.keystore.jks")
            storePassword = "ark2023@cn1"
            keyAlias = "key0"
            keyPassword = "ark2023@cn2"
        }
    }

    defaultConfig {
        applicationId = "com.wyz.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        signingConfig = signingConfigs.getByName("release")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.16.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha13")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.fragment:fragment-ktx:1.8.3")

    // 添加对emlibrary模块的依赖
    implementation(project(":emlibrary"))
    // glide
    implementation("com.github.bumptech.glide:glide:4.15.1")
    // eventbus
    implementation("org.greenrobot:eventbus:3.2.0")
    // OkHttp 库依赖
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
}