plugins {
    alias(libs.plugins.budgetapp.jvm.library)
}

dependencies {
    api(libs.kotlinx.datetime)
    implementation(libs.androidx.annotation.jvm)
}
