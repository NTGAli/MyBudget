package com.ntg.core.data.repository

import com.ntg.core.database.model.ContactEntity
import com.ntg.core.model.Contact

interface ContactRepository {

    suspend fun save(contact: ContactEntity)

    fun findById(id: Long): Contact?

    fun findAll(): List<Contact>

}