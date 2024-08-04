package com.ntg.samples.apps.budgetapp

/**
 * This is shared between :app and :benchmarks module to provide configurations type safety.
 */
enum class BudgetAppBuildType(val applicationIdSuffix: String? = null) {
    DEBUG(".debug"),
    RELEASE,
}
