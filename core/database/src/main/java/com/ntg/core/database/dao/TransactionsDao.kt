package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.ntg.core.database.model.TransactionEntity
import com.ntg.core.model.Transaction

@Dao
interface TransactionsDao {

    @Insert
    suspend fun insert(transactionEntity: TransactionEntity): Long

    @Upsert
    suspend fun update(transactionEntity: TransactionEntity): Long

    @Delete
    suspend fun delete(transactionEntity: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id=:id")
    suspend fun delete(id: Int)

    @Query("""
        SELECT t.*, c.name FROM transactions t
        LEFT JOIN category_table c
        ON c.id = t.categoryId
        WHERE t.sourceId IN (:ids)
        ORDER BY t.createdAt DESC
    """)
    suspend fun getBySourceIds(ids: List<Int>): List<Transaction>


    @Query("DELETE FROM transactions WHERE accountId = :accountId")
    suspend fun deleteByAccount(accountId: Int)

//    @Query("""
//    SELECT
//    t.*,
//    se.data AS walletData,
//    cr.faName,
//    c.name,
//    '[' || GROUP_CONCAT(
//        '{"id":' || ct.id ||
//        ',"fullName":"' || REPLACE(ct.fullName, '"', '\"') ||
//        '","phoneNumber":"' || REPLACE(ct.phoneNumber, '"', '\"') || '"}'
//    , ',') || ']' AS contactsJson
//    FROM transactions t
//    LEFT JOIN category_table c ON c.id = t.categoryId
//    LEFT JOIN wallets se ON se.id = t.sourceId
//    LEFT JOIN currencies cr ON cr.id = se.currencyId
//    LEFT JOIN contacts ct ON ct.transactionId = t.id
//    WHERE t.id = :id
//    GROUP BY t.id
//    """)
//    suspend fun transactionById(id: Int): Transaction?

    @Query("""
    SELECT 
    t.*, 
    se.data AS walletData, 
    cr.faName, 
    c.name
    FROM transactions t
    LEFT JOIN category_table c ON c.id = t.categoryId
    LEFT JOIN wallets se ON se.id = t.sourceId
    LEFT JOIN currencies cr ON cr.id = se.currencyId
    WHERE t.id = :id
    GROUP BY t.id
    """)
    suspend fun transactionById(id: Int): Transaction?

}