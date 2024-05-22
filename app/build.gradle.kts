plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.k2_9.omrekap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.k2_9.omrekap"
        minSdk = 28
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

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
	implementation("com.github.chrisbanes:PhotoView:2.3.0")
	implementation("androidx.activity:activity-ktx:1.8.2")
	implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")

	// Camera
	val cameraxVersion = "1.3.1"
	implementation("androidx.camera:camera-core:${cameraxVersion}")
	implementation("androidx.camera:camera-camera2:${cameraxVersion}")
	implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
	implementation("androidx.camera:camera-view:${cameraxVersion}")
	implementation("androidx.camera:camera-extensions:${cameraxVersion}")

	// JSON
	implementation("com.google.code.gson:gson:2.10.1")

	// OpenCV
	implementation("org.opencv:opencv:4.9.0")

 	// Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
