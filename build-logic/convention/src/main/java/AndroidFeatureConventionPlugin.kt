import com.android.build.gradle.LibraryExtension
import com.ntg.samples.apps.budgetapp.configureGradleManagedDevices
import com.ntg.samples.apps.budgetapp.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("budgetapp.android.library")
                apply("budgetapp.android.hilt")
            }
            extensions.configure<LibraryExtension> {
//                defaultConfig {
//                    testInstrumentationRunner =
//                        "com.ntg.samples.apps.budgetapp.core.testing.BudgetAppTestRunner"
//                }
                testOptions.animationsDisabled = true
                configureGradleManagedDevices(this)
            }

            dependencies {
//                add("implementation", project(":core:ui"))
                add("implementation", project(":core:designsystem"))

                add("implementation", libs.findLibrary("androidx.hilt.navigation.compose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.runtimeCompose").get())
                add("implementation", libs.findLibrary("androidx.lifecycle.viewModelCompose").get())
                add("implementation", libs.findLibrary("androidx.tracing.ktx").get())

//                add("androidTestImplementation", libs.findLibrary("androidx.lifecycle.runtimeTesting").get())
            }
        }
    }
}
