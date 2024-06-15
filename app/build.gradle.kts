import com.android.build.gradle.internal.api.ApkVariantOutputImpl
import org.gradle.kotlin.dsl.support.uppercaseFirstChar



plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "sh.unlimi.x2vx"
    compileSdk = 34
    val applicationName = "sh.unlimi.x2vx"
    val versionMajor = 1
    val versionMinor = 0
    val versionPatch = 2

    defaultConfig {
        applicationId = "sh.unlimi.x2vx"
        minSdk = 29
        targetSdk = 34
        versionCode = versionMinor * 10000 + versionMinor * 100 + versionPatch
        versionName = "${versionMajor}.${versionMinor}.${versionPatch}"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    applicationVariants.configureEach {
        // rename the output APK file
        outputs.configureEach {
            (this as? ApkVariantOutputImpl)?.outputFileName =
                "${applicationName}_${versionName}(${versionCode})_${buildType.name}.apk"
        }

        // rename the output AAB file
        tasks.named(
            "sign${flavorName.uppercaseFirstChar()}${buildType.name.uppercaseFirstChar()}Bundle",
            com.android.build.gradle.internal.tasks.FinalizeBundleTask::class.java
        ) {
            val file = finalBundleFile.asFile.get()
            val finalFile =
                File(
                    file.parentFile,
                    "${rootProject.name}_$versionName($versionCode)_${buildType.name}.aab"
                )
            finalBundleFile.set(finalFile)
        }
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
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.core.splashscreen)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}