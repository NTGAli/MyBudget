package com.ntg.core.mybudget.common

object Constants {

    object InternalApp{
        const val DATABASE_NAME = "budgetApp"
    }

    object SourceExpenseTypes{
        const val ACCOUNT = -1
    }

    object Prefs{
        const val ACCESS_TOKEN = "ACCESS_TOKEN"
        const val SHARED_PREFS_NAME = "budgetShared"
    }

    object Configs{
        const val BANK_LOGO_COLOR_URL = "banks_logo_color_root_url"
        const val BANK_LOGO_MONO_URL = "banks_logo_no_color_root_url"
    }

    object BudgetType {
        const val EXPENSE = 0
        const val INCOME = 1
        const val TRANSFER = 2
        const val INIT = 3
        const val NOTHING = -1
    }

    object FilterTime {
        const val DAY = 0
        const val MONTH = 1
        const val YEAR = 2
    }

    object AttachTyp{
        const val ATTACHED_IMAGE = 0
        const val ATTACHED_DOCUMENT = 1
    }

}