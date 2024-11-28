plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.group12.uistride"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.group12.uistride"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation("org.osmdroid:osmdroid-android:6.1.20")
    implementation ("com.google.android.gms:play-services-location:21.0.1")

    implementation("org.mapsforge:mapsforge-core:0.17.0")
    implementation("org.mapsforge:mapsforge-map:0.17.0")
    implementation("org.mapsforge:mapsforge-map-reader:0.17.0")
    implementation("org.mapsforge:mapsforge-themes:0.17.0")
    implementation("org.mapsforge:mapsforge-map-android:0.17.0")

    implementation ("com.google.android.material:material:1.10.0")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")


}