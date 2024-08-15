package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ntg.core.database.model.AccountEntity
import com.ntg.core.model.AccountWithSources
import com.ntg.core.model.RawAccountWithSource
import com.ntg.core.model.SourceType
import com.ntg.core.model.SourceWithDetail

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

    @Query("SELECT * FROM accounts")
    suspend fun getAll(): List<AccountEntity>

    @Query("SELECT * FROM accounts WHERE isSelected = 1")
    suspend fun currentAccount(): AccountEntity?

    @Query(
        """
        SELECT ae.id as accountId, ae.name as accountName, 
               se.id as sourceId, se.type, se.name,
               bc.number, bc.cvv, bc.date
        FROM accounts ae
        LEFT JOIN sourceExpenditures se ON ae.id = se.accountId
        LEFT JOIN bank_card_entity bc ON se.id = bc.sourceId AND se.type = 0
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
                sources = sources.map { row ->
                    val sourceType = when (row.type) {
                        0 -> SourceType.BankCardSource(
                            cardNumber = row.number ?: "",
                            cvv = row.cvv ?: "",
                            expire = row.expire ?: ""
                        )

                        1 -> SourceType.Gold(
                            value = row.value ?: 0.0,
                            weight = row.weight ?: 0.0
                        )

                        else -> null

//                        else -> throw IllegalArgumentException("Unknown type")
                    }
                    SourceWithDetail(
                        id = row.sourceId ?: 0,
                        accountId = row.accountId,
                        type = row.type ?: 0,
                        name = row.name ?: "",
                        sourceType = sourceType
                    )
                }
            )
        }
    }

}