// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()

        // Android Build Server
        maven { url = uri("../budgetapp-prebuilts/m2repository") }
    }
    dependencies {
        classpath(libs.google.oss.licenses.plugin) {
            exclude(group = "com.google.protobuf")
        }
    }

}

// Lists all plugins used throughout the project
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.baselineprofile) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.dependencyGuard) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.firebase.perf) apply false
    alias(libs.plugins.gms) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.roborazzi) apply false
    alias(libs.plugins.secrets) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.module.graph) apply true
    alias(libs.plugins.diffplugSpotless)
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}


tasks.register("printModulePaths") {
    subprojects {
        if (subprojects.size == 0) {
            println(this.path)
        }
    }
}


//spotless {
//    kotlin {
//        target("**/*.kt", "**/*.kts")
//        targetExclude("${layout.buildDirectory}/**/*.kt", "bin/**/*.kt", "buildSrc/**/*.kt")
//        ktlint().editorConfigOverride(mapOf("max_line_length" to "off"))
//    }
//}
