package com.ntg.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ntg.core.database.convertor.Converters
import com.ntg.core.database.dao.AccountDao
import com.ntg.core.database.dao.BankDao
import com.ntg.core.database.dao.CategoryDao
import com.ntg.core.database.dao.ConfigDao
import com.ntg.core.database.dao.ContactDao
import com.ntg.core.database.dao.CurrencyDao
import com.ntg.core.database.dao.PersonDao
import com.ntg.core.database.dao.WalletsDao
import com.ntg.core.database.dao.TransactionsDao
import com.ntg.core.database.dao.WalletDao
import com.ntg.core.database.model.AccountEntity
import com.ntg.core.database.model.BankEntity
import com.ntg.core.database.model.CategoryEntity
import com.ntg.core.database.model.ConfigEntity
import com.ntg.core.database.model.ContactEntity
import com.ntg.core.database.model.CurrencyEntity
import com.ntg.core.database.model.PersonEntity
import com.ntg.core.database.model.WalletEntity
import com.ntg.core.database.model.TransactionEntity
import com.ntg.core.database.model.WalletTypeEntity

@Database(
    entities = [
        AccountEntity::class,
        WalletEntity::class,
        TransactionEntity::class,
        WalletTypeEntity::class,
        BankEntity::class,
        ConfigEntity::class,
        CurrencyEntity::class,
        CategoryEntity::class,
        ContactEntity::class,
        PersonEntity::class
    ],
    version = 1, exportSchema = true
)
@TypeConverters(Converters::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun sourceDao(): WalletsDao
    abstract fun transactionsDao(): TransactionsDao
    abstract fun walletDao(): WalletDao
    abstract fun bankDao(): BankDao
    abstract fun configDao(): ConfigDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun categoryDao(): CategoryDao
    abstract fun contactDao(): ContactDao
    abstract fun personDao(): PersonDao
}