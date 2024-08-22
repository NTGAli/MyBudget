plugins {
    alias(libs.plugins.budgetapp.android.feature)
    alias(libs.plugins.budgetapp.android.library.compose)
    alias(libs.plugins.budgetapp.android.library.jacoco)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.ntg.feature.home"
}

dependencies {
    implementation(libs.accompanist.permissions)
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))

    implementation(libs.gson)

//    testImplementation(libs.robolectric)
    testDemoImplementation(libs.roborazzi)
}
