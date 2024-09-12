package com.ntg.mybudget.sync.work.workers

import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.ntg.mybudget.sync.work.workers.workers.SyncWorker
import javax.inject.Inject

class SyncDataImpl
    @Inject constructor()
    : SyncData {
    override fun sync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(true)
            .build()
        val syncWorkRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()
        WorkManager
            .initialize(
                context,
                Configuration.Builder().setMinimumLoggingLevel(Log.DEBUG).build()
            )
        WorkManager
            .getInstance(context).enqueue(syncWorkRequest)

    }
}