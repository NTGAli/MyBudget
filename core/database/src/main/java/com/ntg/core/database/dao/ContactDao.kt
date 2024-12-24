package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.ntg.core.database.model.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Insert
    suspend fun insertAll(contacts: List<ContactEntity>)

    @Upsert
    suspend fun upsert(contact: ContactEntity)

    @Upsert
    suspend fun upsertAll(contacts: List<ContactEntity>)

    @Query("SELECT * FROM contacts")
    fun getContacts(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE phoneNumber in (:ids)")
    fun getContacts(ids: List<String>): Flow<List<ContactEntity>>

    @Query("DELETE FROM contacts WHERE phoneNumber = :id")
    suspend fun deleteContact(id: String)

    @Query("UPDATE contacts SET fullName = :name, phoneNumber = :phone WHERE phoneNumber = :id")
    suspend fun updateContact(id: String, name: String, phone: String)

    @Query("SELECT * FROM contacts WHERE phoneNumber = :id")
    suspend fun getContact(id: String): ContactEntity

    @Query("SELECT * FROM contacts WHERE isSynced = 0")
    fun getUnSyncedContacts(): Flow<List<ContactEntity>>

    @Query("UPDATE contacts SET isSynced = 1, sId=:sId where phoneNumber=:id")
    suspend fun synced(id: String, sId: String)
}