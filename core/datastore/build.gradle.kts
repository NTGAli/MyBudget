plugins {
    alias(libs.plugins.budgetapp.android.library)
    alias(libs.plugins.budgetapp.android.library.jacoco)
    alias(libs.plugins.budgetapp.android.hilt)
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    namespace = "com.nt.com.core.datastore"
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    api(libs.androidx.dataStore)

    api(project(":core:datastore-proto"))
    api(project(":core:model"))

    implementation(projects.core.common)

}
