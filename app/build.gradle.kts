import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

android {
    namespace = "com.example.weatherapp"
    compileSdk = 35
    android.buildFeatures.buildConfig = true

    defaultConfig {
        applicationId = "com.example.weatherapp"
        minSdk = 33
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val weatherApiKey = localProperties.getProperty("WEATHER_API_KEY")
        if (weatherApiKey == null) {
            println("Warning: WEATHER_API_KEY not found in local.properties. API calls might fail.")
        }
        buildConfigField("String", "WEATHER_API_KEY", "\"${weatherApiKey ?: ""}\"") //
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
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Retrofit do obsługi API (popularna biblioteka)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Lub inny konwerter np. Moshi, Kotlinx Serialization
    // OkHttp (Retrofit używa go pod spodem, warto dodać jawnie dla logowania np.)
    implementation("com.squareup.okhttp3:okhttp:4.11.0") // Sprawdź najnowszą wersję
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0") // Do logowania zapytań/odpowiedzi (opcjonalne, ale przydatne)

    // Kotlin Coroutines do obsługi operacji asynchronicznych
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Sprawdź najnowszą wersję

    // ViewModel i LiveData/StateFlow do zarządzania stanem i cyklem życia
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0") // Sprawdź najnowszą wersję
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Moshi
    implementation("com.squareup.moshi:moshi:1.15.0") // Główna biblioteka Moshi, sprawdź najnowszą wersję
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

    // Jeśli chcesz używać Moshi z Retrofit, potrzebujesz konwertera Moshi dla Retrofit:
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0") // Sprawdź najnowszą wersję

    implementation("androidx.compose.material:material-icons-core:1.6.7")
    implementation("androidx.compose.material:material-icons-extended:1.6.7")

    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.1")

    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
}