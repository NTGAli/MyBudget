package com.ntg.core.data.repository

import com.ntg.core.database.model.ContactEntity
import com.ntg.core.model.Contact
import com.ntg.core.model.res.Category
import kotlinx.coroutines.flow.Flow

interface ContactRepository {

    suspend fun save(contact: ContactEntity)

    suspend fun upsertContact(contact: Contact)

    fun findById(id: Long): Contact?

    fun findAll(): List<Contact>

    suspend fun getAll(): Flow<List<Contact>?>

    suspend fun get(contacts: List<String>?): Flow<List<Contact>?>

    suspend fun syncContacts()

    suspend fun updateWithServer()

}