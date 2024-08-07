plugins {
    alias(libs.plugins.budgetapp.android.library)
    alias(libs.plugins.budgetapp.android.library.jacoco)
    alias(libs.plugins.budgetapp.android.hilt)
}

android {
    namespace = "com.ntg.core.common"
}

dependencies {
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    implementation(libs.androidx.runtime.livedata)
}
