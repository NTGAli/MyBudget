package com.ntg.mybudget.sync.work.workers.status

import android.util.Log
import javax.inject.Inject

/**
 * Stub implementation of [SyncSubscriber]
 */
class StubSyncSubscriber @Inject constructor() : SyncSubscriber {
    override suspend fun subscribe() {
        Log.d("SYNC", "Subscribing to sync")
    }
}