package com.ntg.core.database.di

import com.ntg.core.database.AppDatabase
import com.ntg.core.database.dao.AccountDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object DaosModule {

    @Provides
    fun provideAccountDao(
        database: AppDatabase
    ): AccountDao = database.accountDao()

}