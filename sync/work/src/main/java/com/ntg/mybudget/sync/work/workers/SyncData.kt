package com.ntg.mybudget.sync.work.workers

import android.content.Context


interface SyncData {
    fun sync(context: Context)
}