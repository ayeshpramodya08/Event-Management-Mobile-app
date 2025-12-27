plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // Google Services ප්ලගිනය
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.comexampleap"
    compileSdk = 36 // සාමාන්‍යයෙන් 35 හෝ 34 භාවිතා කිරීම වඩාත් ස්ථායී වේ (Stable)

    defaultConfig {
        applicationId = "com.example.comexampleap"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // --- Firebase Dependencies (නිවැරදි කරන ලද කොටස) ---
    // Firebase BoM (Bill of Materials) භාවිතා කිරීම - සියලුම Firebase versions පාලනය කරන්නේ මෙයයි.
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))

    // Authentication (Authentication සඳහා දැන් -ktx අවශ්‍ය නැත)
    implementation("com.google.firebase:firebase-auth")

    // Realtime Database (Database සඳහා දැන් -ktx අවශ්‍ය නැත)
    implementation("com.google.firebase:firebase-database")

    // Analytics (විකල්ප - analytics අවශ්‍ය නම් පමණක් තබා ගන්න)
    implementation("com.google.firebase:firebase-analytics")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
