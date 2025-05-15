import org.jetbrains.kotlin.serialization.js.ast.JsAstProtoBuf.TrueLiteral

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}
val tomtomApiKey: String by project
android {
    namespace = "com.ganaa.carcompanion"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ganaa.carcompanion"
        minSdk = 26
        targetSdk = 35
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
    buildTypes.configureEach{
        buildConfigField("String", "TOMTOM_API_KEY","\"$tomtomApiKey\"")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.provider.default)
    implementation(libs.provider.map.matched)
    implementation(libs.provider.simulation)
    implementation(libs.map.display)
    implementation(libs.navigation.tile.store)
    implementation(libs.navigation.online)
    implementation(libs.ui)
    val version = "1.24.2"
    implementation("com.tomtom.sdk.navigation:navigation-online:$version")
    implementation("com.tomtom.sdk.routing:route-planner-online:1.22.4")
    implementation("com.tomtom.sdk.search:search-online:1.24.2")

}