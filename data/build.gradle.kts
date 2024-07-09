import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kapt)
}

android {
    namespace = "hiendao.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val keystore = project.rootProject.file("hiendao.properties")
        val properties = Properties()
        properties.load(keystore.inputStream())

        val apiKey = properties.getProperty("API_KEY") ?: ""
        val accessToken = properties.getProperty("ACCESS_TOKEN") ?: ""

        buildConfigField(
            type = "String",
            name = "API_KEY",
            value = apiKey
        )

        buildConfigField(
            type = "String",
            name = "ACCESS_TOKEN",
            value = accessToken
        )
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
    buildFeatures{
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    //Retrofit
    implementation(libs.squareup.retrofit)
    implementation(libs.squareup.moshi)
    implementation(libs.squareup.okhttp)
    implementation(libs.squareup.interceptor)

    //Lifecycle viewmodel
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.runtime)
}