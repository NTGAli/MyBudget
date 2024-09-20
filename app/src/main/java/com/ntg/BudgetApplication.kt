package com.ntg

import android.app.Application
import com.ntg.mybudget.sync.work.workers.initializers.Sync
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BudgetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Sync.initialize(context = this)
        Sync.updateConfigs(this)
    }


}
