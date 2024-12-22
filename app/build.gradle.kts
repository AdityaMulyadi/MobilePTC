plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.mobileptc"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mobileptc"
        minSdk = 26
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        mlModelBinding = true
    }
    sourceSets {
        getByName("main") {
            assets {
                srcDirs("src/main/assets")
            }
        }
    }
    aaptOptions {
        noCompress("tflite")
    }
}

dependencies {
//    implementation("com.google.android.gms:play-services-tflite-support:16.1.0")
//    implementation("com.google.android.gms:play-services-tflite-java:16.1.0")
//    implementation("org.tensorflow:tensorflow-lite:1.12.0")
    implementation("org.tensorflow:tensorflow-lite-select-tf-ops:0.0.0-nightly-SNAPSHOT")
    implementation("org.apache.commons:commons-math3:3.6.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
//    implementation(libs.google.litert)
    implementation(libs.litert)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}