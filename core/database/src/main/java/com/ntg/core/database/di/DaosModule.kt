package com.ntg.core.database.di

import com.ntg.core.database.AppDatabase
import com.ntg.core.database.dao.AccountDao
import com.ntg.core.database.dao.BankCardDao
import com.ntg.core.database.dao.SourceExpenditureDao
import com.ntg.core.database.dao.TransactionsDao
import com.ntg.core.database.dao.WalletDao
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

    @Provides
    fun provideSourceExpenditure(
        database: AppDatabase
    ): SourceExpenditureDao = database.sourceDao()

    @Provides
    fun provideBankCard(
        database: AppDatabase
    ): BankCardDao = database.bankCardDao()

    @Provides
    fun provideTransactions(
        database: AppDatabase
    ): TransactionsDao = database.transactionsDao()

    @Provides
    fun provideWalletTypes(
        database: AppDatabase
    ): WalletDao = database.walletDao()

}