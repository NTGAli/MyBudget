package com.ntg.mybudget.sync.work.workers.initializers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.ntg.mybudget.sync.work.workers.workers.ConfigWorker
import com.ntg.mybudget.sync.work.workers.workers.SyncWorker

object Sync {
    // This method is initializes sync, the process that keeps the app's data current.
    // It is called from the app module's Application.onCreate() and should be only done once.
    fun initialize(context: Context) {
        WorkManager.getInstance(context).apply {
            // Run sync on app startup and ensure only one sync worker runs at any time
            enqueueUniqueWork(
                SYNC_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                SyncWorker.startUpSyncWork(),
            )
        }
    }

    fun updateConfigs(context: Context){
        WorkManager.getInstance(context).apply {
            // Run sync on app startup and ensure only one sync worker runs at any time
            enqueueUniqueWork(
                CONFIG_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                ConfigWorker.startUpSyncWork(),
            )
        }
    }
}

// This name should not be changed otherwise the app may have concurrent sync requests running
internal const val SYNC_WORK_NAME = "SyncWorkName"
internal const val CONFIG_WORK_NAME = "ConfigWorkName"
