plugins {
    alias(libs.plugins.budgetapp.android.library)
    alias(libs.plugins.budgetapp.android.library.jacoco)
    alias(libs.plugins.budgetapp.android.hilt)
}

android {
    namespace = "com.ntg.core.common"
}

dependencies {
    implementation(projects.core.model)
//    testImplementation(libs.kotlinx.coroutines.test)
//    testImplementation(libs.turbine)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.gson)
}
