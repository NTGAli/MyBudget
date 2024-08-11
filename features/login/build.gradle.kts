plugins {
    alias(libs.plugins.budgetapp.android.feature)
    alias(libs.plugins.budgetapp.android.library.compose)
    alias(libs.plugins.budgetapp.android.library.jacoco)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.ntg.feature.login"
}

dependencies {
    implementation(project(":core:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:model"))
    implementation(libs.androidx.runtime.livedata)
}
