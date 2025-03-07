plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.androidx.navigation.safeargs)
    id("kotlin-kapt")
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.idz.ChallengeZone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.idz.ChallengeZone"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "CLOUD_NAME", "\"${project.properties["CLOUD_NAME"] ?: ""}\"")
        buildConfigField("String", "API_KEY", "\"${project.properties["API_KEY"] ?: ""}\"")
        buildConfigField("String", "API_SECRET", "\"${project.properties["API_SECRET"] ?: ""}\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"${project.properties["GEMINI_API_KEY"] ?: ""}\"")


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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui )
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.firebase.storage)
    implementation(libs.picasso)
    implementation(libs.cloudinary.android)
    implementation(libs.firebase.firestore)
    implementation(platform(libs.firebase.bom))
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.firebase:firebase-auth")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}