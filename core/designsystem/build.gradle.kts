plugins {
    alias(libs.plugins.budgetapp.android.library)
    alias(libs.plugins.budgetapp.android.library.compose)
    alias(libs.plugins.budgetapp.android.library.jacoco)
    alias(libs.plugins.roborazzi)
}

android {
//    defaultConfig {
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//    }
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    namespace = "com.ntg.mybudget.core.designsystem"
}

dependencies {

    implementation(project(":core:common"))
    implementation(project(":core:model"))
    
    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.graphics.android)
    implementation(libs.androidx.compose.runtime)
    api(libs.androidx.compose.ui.util)
    implementation(libs.coil.kt)

    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.svg)
    implementation(libs.lottie)



//    testImplementation(libs.androidx.compose.ui.test)
//    testImplementation(libs.hilt.android.testing)
//    testImplementation(libs.robolectric)
//    testImplementation(libs.roborazzi)

//    androidTestImplementation(libs.androidx.compose.ui.test)
    implementation(libs.androidx.activity.compose.v182)
}
