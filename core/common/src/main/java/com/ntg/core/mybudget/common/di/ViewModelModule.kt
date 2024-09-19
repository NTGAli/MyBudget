package com.ntg.core.mybudget.common.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.ntg.core.mybudget.common.Constants
import com.ntg.core.mybudget.common.SharedViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {
    @Provides
    @Singleton
    fun provideSharedViewModel(): SharedViewModel {
        return SharedViewModel()
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(
        application: Application
    ): SharedPreferences {
        return application.getSharedPreferences(
            Constants.Prefs.SHARED_PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

}