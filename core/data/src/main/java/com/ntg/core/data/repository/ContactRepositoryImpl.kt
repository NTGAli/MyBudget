package com.ntg.core.data.repository

import com.ntg.core.database.dao.ContactDao
import com.ntg.core.database.model.ContactEntity
import com.ntg.core.database.model.toContactEntity
import com.ntg.core.model.Contact
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao
): ContactRepository {
    override suspend fun save(contact: ContactEntity) {
        contactDao.insertContact(contact)
    }

    override fun findById(id: Long): Contact? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<Contact> {
        TODO("Not yet implemented")
    }
}