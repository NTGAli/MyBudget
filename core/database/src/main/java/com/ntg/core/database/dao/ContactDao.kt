package com.ntg.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ntg.core.database.model.ContactEntity

@Dao
interface ContactDao {

    @Insert
    suspend fun insertContact(contact: ContactEntity)

    @Query("SELECT * FROM contacts")
    suspend fun getContacts(): List<ContactEntity>

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteContact(id: Int)

    @Query("UPDATE contacts SET fullName = :name, phoneNumber = :phone WHERE id = :id")
    suspend fun updateContact(id: Int, name: String, phone: String)

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContact(id: Int): ContactEntity

    @Query("SELECT * FROM contacts WHERE isSynced = 0")
    fun getUnsyncedContacts(): List<ContactEntity>

    @Query("SELECT * FROM contacts WHERE transactionId = id")
    fun getContactByTransaction(): List<ContactEntity>
}