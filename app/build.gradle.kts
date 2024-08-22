import com.ntg.samples.apps.budgetapp.BudgetAppBuildType

plugins {
    alias(libs.plugins.budgetapp.android.application)
    alias(libs.plugins.budgetapp.android.application.compose)
    alias(libs.plugins.budgetapp.android.application.flavors)
    alias(libs.plugins.budgetapp.android.application.jacoco)
    alias(libs.plugins.budgetapp.android.hilt)
    alias(libs.plugins.budgetapp.android.application.firebase)
    id("com.google.android.gms.oss-licenses-plugin")
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.roborazzi)
}

android {
    defaultConfig {
        applicationId = "com.ntg.mybudget"
        versionCode = 8
        versionName = "1.0.0"

        // Custom test runner to set up Hilt dependency graph
//        testInstrumentationRunner = "com.ntg.core.testing.BudgetTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = BudgetAppBuildType.DEBUG.applicationIdSuffix
        }
        release {
            isMinifyEnabled = true
            applicationIdSuffix = BudgetAppBuildType.RELEASE.applicationIdSuffix
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            // To publish on the Play store a private signing key is required, but to allow anyone
            // who clones the code to sign and run the release variant, use the debug signing key.
            // TODO: Abstract the signing configuration to a separate file to avoid hardcoding this.
            signingConfig = signingConfigs.named("debug").get()
            // Ensure Baseline Profile is fresh for release builds.
            baselineProfile.automaticGenerationDuringBuild = true
        }
    }

    packaging {
        resources {
            excludes.add("/META-INF/{AL2.0,LGPL2.1}")
        }
    }
//    testOptions {
//        unitTests {
//            isIncludeAndroidResources = true
//        }
//    }
    namespace = "com.ntg.mybudget"
}

dependencies {

    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:data"))
    implementation(project(":core:database"))
    implementation(project(":features:login"))
    implementation(project(":features:setup"))
    implementation(project(":features:home"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3.adaptive)
    implementation(libs.androidx.compose.material3.adaptive.layout)
    implementation(libs.androidx.compose.material3.adaptive.navigation)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.runtime.tracing)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.tracing.ktx)
    implementation(libs.androidx.window.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.coil.kt)

    ksp(libs.hilt.compiler)

    debugImplementation(libs.androidx.compose.ui.testManifest)

    kspTest(libs.hilt.compiler)

    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.work.testing)

    testDemoImplementation(libs.robolectric)
    testDemoImplementation(libs.roborazzi)

//    androidTestImplementation(libs.androidx.test.espresso.core)
//    androidTestImplementation(libs.androidx.navigation.testing)
//    androidTestImplementation(libs.androidx.compose.ui.test)
//    androidTestImplementation(libs.hilt.android.testing)

    implementation(libs.androidx.compose.material3)



}

baselineProfile {
    // Don't build on every iteration of a full assemble.
    // Instead enable generation directly for the release build variant.
    automaticGenerationDuringBuild = false
}

