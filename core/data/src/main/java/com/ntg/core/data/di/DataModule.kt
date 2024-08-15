package com.ntg.core.data.di

import com.ntg.core.data.repository.AccountRepository
import com.ntg.core.data.repository.AccountRepositoryImpl
import com.ntg.core.data.repository.BankCardRepository
import com.ntg.core.data.repository.BankCardRepositoryImpl
import com.ntg.core.data.repository.SourceExpenditureRepository
import com.ntg.core.data.repository.SourceExpenditureRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {

    @Binds
    internal abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    internal abstract fun bindSourceRepository(impl: SourceExpenditureRepositoryImpl): SourceExpenditureRepository

    @Binds
    internal abstract fun bindBankCardRepository(impl: BankCardRepositoryImpl): BankCardRepository

}