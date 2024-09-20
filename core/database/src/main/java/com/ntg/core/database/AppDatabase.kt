package com.ntg.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ntg.core.database.dao.AccountDao
import com.ntg.core.database.dao.BankCardDao
import com.ntg.core.database.dao.BankDao
import com.ntg.core.database.dao.SourceExpenditureDao
import com.ntg.core.database.dao.TransactionsDao
import com.ntg.core.database.dao.WalletDao
import com.ntg.core.database.model.AccountEntity
import com.ntg.core.database.model.BankCardEntity
import com.ntg.core.database.model.SourceExpenditureEntity
import com.ntg.core.database.model.TransactionEntity
import com.ntg.core.database.model.WalletTypeEntity
import com.ntg.core.model.res.Bank

@Database(
    entities = [
        AccountEntity::class,
        SourceExpenditureEntity::class,
        BankCardEntity::class,
        TransactionEntity::class,
        WalletTypeEntity::class,
        Bank::class
    ],
    version = 4, exportSchema = true
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun sourceDao(): SourceExpenditureDao
    abstract fun bankCardDao(): BankCardDao
    abstract fun transactionsDao(): TransactionsDao
    abstract fun walletDao(): WalletDao
    abstract fun bankDao(): BankDao
}