pluginManagement {
    repositories {
        includeBuild("build-logic")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}


rootProject.name = "MyBudget"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:designsystem")
include(":features:login")
include(":core:common")
include(":features:setup")
include(":core:database")
include(":core:model")
include(":core:data")
include(":core:datastore")
include(":core:datastore-proto")
include(":features:home")
include(":core:network")
include(":sync")
include(":sync:work")
include(":features:profile")
include(":features:report")
