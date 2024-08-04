package com.ntg.samples.apps.budgetapp

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.ApplicationProductFlavor
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ProductFlavor

@Suppress("EnumEntryName")
enum class BudgetAppDimension {
    contentType
}

// The content for the app can either come from local static data which is useful for demo
// purposes, or from a production backend server which supplies up-to-date, real content.
// These two product flavors reflect this behaviour.
@Suppress("EnumEntryName")
enum class BudgetAppFlavor(val dimension: BudgetAppDimension, val applicationIdSuffix: String? = null) {
    demo(BudgetAppDimension.contentType, applicationIdSuffix = ".demo"),
    prod(BudgetAppDimension.contentType)
}

fun configureFlavors(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
    flavorConfigurationBlock: ProductFlavor.(flavor: BudgetAppFlavor) -> Unit = {}
) {
    commonExtension.apply {
        flavorDimensions += BudgetAppDimension.contentType.name
        productFlavors {
            BudgetAppFlavor.values().forEach {
                create(it.name) {
                    dimension = it.dimension.name
                    flavorConfigurationBlock(this, it)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        if (it.applicationIdSuffix != null) {
                            applicationIdSuffix = it.applicationIdSuffix
                        }
                    }
                }
            }
        }
    }
}
