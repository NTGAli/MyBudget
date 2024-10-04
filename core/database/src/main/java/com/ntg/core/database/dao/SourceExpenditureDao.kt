package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ntg.core.database.model.SourceExpenditureEntity
import com.ntg.core.model.RawSourceDetail
import com.ntg.core.model.SourceType
import com.ntg.core.model.SourceWithDetail
import kotlinx.coroutines.flow.Flow

@Dao
interface SourceExpenditureDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(sourceExpenditure: SourceExpenditureEntity)

    @Delete
    suspend fun delete(sourceExpenditure: SourceExpenditureEntity)

    @Query("DELETE FROM sourceExpenditures WHERE accountId=:accountId")
    suspend fun forceDelete(accountId: Int)

    @Query("SELECT * FROM sourceExpenditures")
    suspend fun getAll(): List<SourceExpenditureEntity>

    @Query("SELECT * FROM sourceExpenditures WHERE accountId=:accountId")
    suspend fun getSources(accountId: Int): List<SourceExpenditureEntity>

    @Query("SELECT * FROM sourceExpenditures WHERE id=:id")
    suspend fun getSource(id: Int): SourceExpenditureEntity?

    @Query("UPDATE sourceExpenditures SET isRemoved=1, isSynced=0 WHERE id=:id")
    suspend fun tempRemove(id: Int)

    @Transaction
    @Query("""
        SELECT se.id, se.accountId, se.type, cr.nativeName as name,
        bc.number, bc.cvv, bc.sheba, bc.accountNumber, bc.date as expire, bc.name as cardName, bc.id as bankId
        FROM sourceExpenditures se
        LEFT JOIN bank_card_entity bc ON se.id = bc.sourceId
        LEFT JOIN currencies cr ON se.currencyId = cr.id
        WHERE se.isRemoved != 1
    """)
    suspend fun getSourcesByAccount(): List<RawSourceDetail>

    @Transaction
    suspend fun getSourcesWithDetails(accountId: Int): List<SourceWithDetail> {
        return getSourcesByAccount().map { row ->
            val sourceType = when (row.type) {
                1 -> SourceType.BankCard(
                    id = row.bankId ?: -1,
                    number = row.number ?: "",
                    cvv = row.cvv ?: "",
                    date = row.expire ?: "",
                    name = row.cardName.orEmpty(),
                    sheba = row.sheba.orEmpty(),
                    accountNumber = row.accountNumber.orEmpty(),
                )

                3 -> SourceType.Gold(
                    value = row.value ?: 0.0,
                    weight = row.weight ?: 0.0
                )

                else -> throw IllegalArgumentException("Unknown type")
            }
            SourceWithDetail(
                id = row.id,
                accountId = row.accountId,
                type = row.type,
                name = row.name,
                sourceType = sourceType
            )
        }
    }


    @Transaction
    @Query("""
        SELECT se.id, se.accountId, se.type, cr.nativeName as name,
        bc.number, bc.cvv, bc.date as expire, bc.name as cardName, bc.id as bankId, bc.sheba, bc.accountNumber
        FROM sourceExpenditures se
        LEFT JOIN bank_card_entity bc ON se.id = bc.sourceId
        LEFT JOIN currencies cr ON se.currencyId = cr.id
        WHERE se.id=:id
    """)
    suspend fun getSourceWithDetails(id: Int): RawSourceDetail?

    @Transaction
    suspend fun getSourceDetails(id: Int): SourceWithDetail? {

        val row = getSourceWithDetails(id)
        if (row != null) {
            val sourceType = when (row.type) {
                1 -> SourceType.BankCard(
                    id = row.bankId ?: -1,
                    number = row.number ?: "",
                    cvv = row.cvv ?: "",
                    sheba = row.sheba ?: "",
                    accountNumber = row.accountNumber ?: "",
                    date = row.expire ?: "",
                    name = row.cardName.orEmpty()
                )

                2 -> SourceType.Gold(
                    value = row.value ?: 0.0,
                    weight = row.weight ?: 0.0
                )

                else -> throw IllegalArgumentException("Unknown type")
            }
            return SourceWithDetail(
                id = row.id,
                accountId = row.accountId,
                type = row.type,
                name = row.name,
                sourceType = sourceType
            )
        } else {
            return null
        }
    }


    @Query("UPDATE sourceExpenditures SET isSelected = CASE WHEN id IN (:ids) THEN 1 ELSE 0 END")
    suspend fun updateSelectedSources(ids: List<Int>)

//    @Query("SELECT * FROM sourceExpenditures WHERE isSelected = 1")
//    suspend fun getSelectedSources(): List<SourceWithDetail>

    @Transaction
    @Query(
        "SELECT se.id, se.accountId, se.type,\n" +
                "            bc.number, bc.cvv, bc.date as expire, bc.name as cardName, bc.id as bankId, bc.sheba, bc.accountNumber\n" +
                "        FROM sourceExpenditures se\n" +
                "        LEFT JOIN bank_card_entity bc ON se.id = bc.sourceId AND se.type = 0\n" +
                " WHERE se.isSelected = 1"
    )
    suspend fun getSelectedSources(): List<RawSourceDetail>

    @Query("""
        SELECT se.id, se.sId, se.accountId, se.type, cr.nativeName as name, se.currencyId, se.isRemoved,
        bc.number, bc.cvv, bc.sheba, bc.accountNumber, bc.date as expire, bc.name as cardName, bc.bankId as bankId,
        b.nativeName as bankName, ac.sId as accountSId
        FROM sourceExpenditures se
        LEFT JOIN bank_card_entity bc ON se.id = bc.sourceId
        LEFT JOIN currencies cr ON se.currencyId = cr.id
        LEFT JOIN banks b ON bc.bankId = b.id
        LEFT JOIN accounts ac ON ac.id = se.accountId
        WHERE se.isSynced = 0
    """)
    fun unSynced(): Flow<List<RawSourceDetail>>

    @Query("UPDATE sourceExpenditures SET sId=:sId, isSynced=1 WHERE id=:id")
    suspend fun sync(id: Int, sId: String)
}