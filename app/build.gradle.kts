import java.time.LocalDate
import java.time.format.DateTimeFormatter

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.temperature.temperatur_sensor_sdk"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.temperature.temperatur_sensor_sdk"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            resValue("string", "temperature_sensor_app", "temperature_sensor_app")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            applicationVariants.all {
                outputs.all {
                    val output = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
                    val localDate = LocalDate.now()
                    val formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                    output.outputFileName = "temperature_sensor_app_v${versionName}_${buildType.name}_${formattedDate}.apk"
                }
            }
        }

        debug {
            resValue("string", "temperature_sensor_app_debug", "temperature_sensor_app_debug")
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

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
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
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // room
    // Room 主要的核心依賴
    implementation(libs.androidx.room.runtime)

    // 如果使用 Kotlin，加入 Kapt 支援（用於編譯時生成代碼）
    ksp(libs.androidx.room.compiler)

    // 如果需要使用 RxJava 支援（可選）
    implementation (libs.androidx.room.rxjava3)

    // 如果需要使用 Kotlin Coroutines 支援（可選）
    implementation (libs.androidx.room.ktx)

    // 如果需要測試 Room（可選）
    testImplementation (libs.androidx.room.testing)

    implementation(libs.mpandroidchart)

    implementation(libs.datetime)

    implementation(libs.androidx.bluetooth)

    implementation(libs.gson)
}