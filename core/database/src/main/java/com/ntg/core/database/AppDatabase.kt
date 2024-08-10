package com.ntg.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ntg.core.database.dao.AccountDao
import com.ntg.core.database.model.AccountEntity

@Database(
    entities = [
        AccountEntity::class
    ],
    version = 1, exportSchema = true
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
}