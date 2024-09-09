package com.ntg.core.network.di

import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.retrofit.RetrofitBudgetNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredNetworkModule {

    @Binds
    fun binds(impl: RetrofitBudgetNetwork): BudgetNetworkDataSource
}
