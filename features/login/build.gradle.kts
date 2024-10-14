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
    implementation(projects.core.designsystem)
    implementation(projects.core.common)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.sync.work)
    implementation(libs.androidx.runtime.livedata)
}
