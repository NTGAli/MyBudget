package com.ntg.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ntg.core.database.dao.AccountDao
import com.ntg.core.database.dao.BankCardDao
import com.ntg.core.database.dao.SourceExpenditureDao
import com.ntg.core.database.model.AccountEntity
import com.ntg.core.database.model.BankCardEntity
import com.ntg.core.database.model.SourceExpenditureEntity

@Database(
    entities = [
        AccountEntity::class,
        SourceExpenditureEntity::class,
        BankCardEntity::class
    ],
    version = 6, exportSchema = true
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun sourceDao(): SourceExpenditureDao
    abstract fun bankCardDao(): BankCardDao
}