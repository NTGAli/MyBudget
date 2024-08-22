package com.ntg.core.database.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.ntg.core.database.model.AccountEntity
import com.ntg.core.model.AccountWithSources
import com.ntg.core.model.RawAccountWithSource
import com.ntg.core.model.SourceType
import com.ntg.core.model.SourceWithDetail

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: AccountEntity)

    @Delete
    suspend fun delete(account: AccountEntity)

    @Upsert
    suspend fun upsert(account: AccountEntity)

    @Update
    suspend fun update(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE id=:id")
    suspend fun getAccount(id: Int): AccountEntity?

    @Query("SELECT * FROM accounts WHERE sId=:sId")
    suspend fun getAccount(sId: String): AccountEntity?

    @Query("SELECT * FROM accounts")
    suspend fun getAll(): List<AccountEntity>

    @Query("SELECT * FROM accounts WHERE isSelected = 1")
    suspend fun currentAccount(): AccountEntity?

    @Query(
        """
        SELECT ae.id as accountId, ae.name as accountName, 
               se.id as sourceId, se.type, se.name,
               bc.number, bc.cvv, bc.date, bc.id as bankId, bc.name
        FROM accounts ae
        LEFT JOIN sourceExpenditures se ON ae.id = se.accountId AND se.isRemoved = 0
        LEFT JOIN bank_card_entity bc ON se.id = bc.sourceId AND se.type = 0 AND bc.isDeleted = 0
        WHERE ae.isRemoved = 0
        """
    )
    suspend fun getAccountsWithSourcesRaw(): List<RawAccountWithSource>

    @Transaction
    suspend fun getAccountBySources(): List<AccountWithSources> {
        val rawData = getAccountsWithSourcesRaw()

        val accountsMap = rawData.groupBy { it.accountId }

        return accountsMap.map { (accountId, sources) ->
            AccountWithSources(
                accountId = accountId,
                accountName = sources.first().accountName,
                sources = if (sources.first().sourceId != null){
                    sources.map { row ->
                        val sourceType = when (row.type) {
                            0 -> {
                                if (row.number != null){
                                    SourceType.BankCard(
                                        id = row.bankId ?: -1,
                                        number = row.number ?: "",
                                        cvv = row.cvv ?: "",
                                        date = row.expire ?: "",
                                        name = row.name.orEmpty()
                                    )
                                }else null
                            }

                            1 -> SourceType.Gold(
                                value = row.value ?: 0.0,
                                weight = row.weight ?: 0.0
                            )

                            else -> null

                        }
                        if (sourceType != null){
                            SourceWithDetail(
                                id = row.sourceId ?: 0,
                                accountId = row.accountId,
                                type = row.type ?: 0,
                                name = row.name ?: "",
                                sourceType = sourceType
                            )
                        }else null
                    }
                }else emptyList()
            )
        }
    }

}