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
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.sync.work)


    implementation(libs.gson)

//    testImplementation(libs.robolectric)
    testDemoImplementation(libs.roborazzi)
}
