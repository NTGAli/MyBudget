package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ntg.core.database.model.AccountEntity

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE id=:id")
    suspend fun getAccount(id: Int): AccountEntity?

    @Query("SELECT * FROM accounts WHERE sId=:sId")
    suspend fun getAccount(sId: String): AccountEntity?
}