package com.ntg.core.database.di

import com.ntg.core.database.AppDatabase
import com.ntg.core.database.dao.AccountDao
import com.ntg.core.database.dao.BankDao
import com.ntg.core.database.dao.CategoryDao
import com.ntg.core.database.dao.ConfigDao
import com.ntg.core.database.dao.ContactDao
import com.ntg.core.database.dao.CurrencyDao
import com.ntg.core.database.dao.WalletsDao
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
    ): WalletsDao = database.sourceDao()


    @Provides
    fun provideTransactions(
        database: AppDatabase
    ): TransactionsDao = database.transactionsDao()

    @Provides
    fun provideWalletTypes(
        database: AppDatabase
    ): WalletDao = database.walletDao()

    @Provides
    fun provideConfigDao(
        database: AppDatabase
    ): ConfigDao = database.configDao()

    @Provides
    fun provideBankDao(
        database: AppDatabase
    ): BankDao = database.bankDao()


    @Provides
    fun provideCurrencies(
        database: AppDatabase
    ): CurrencyDao = database.currencyDao()

    @Provides
    fun provideCategory(
        database: AppDatabase
    ): CategoryDao = database.categoryDao()

    @Provides
    fun provideContact(
        database: AppDatabase
    ): ContactDao = database.contactDao()

}