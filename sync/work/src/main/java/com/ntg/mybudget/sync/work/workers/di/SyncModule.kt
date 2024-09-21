package com.ntg.mybudget.sync.work.workers.di

import com.ntg.mybudget.sync.work.workers.SyncData
import com.ntg.mybudget.sync.work.workers.SyncDataImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
internal interface SyncModule {
    @Binds
    fun bindSync(impl: SyncDataImpl): SyncData

}