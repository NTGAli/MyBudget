package com.ntg.core.data.repository

import com.ntg.core.database.dao.ContactDao
import com.ntg.core.database.model.ContactEntity
import com.ntg.core.database.model.toContact
import com.ntg.core.database.model.toContactEntity
import com.ntg.core.model.Contact
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao,
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val networkDataSource: BudgetNetworkDataSource,
    ): ContactRepository {
    override suspend fun save(contact: ContactEntity) {
        contactDao.insertContact(contact)
    }

    override suspend fun upsertContact(contact: Contact) {
        contactDao.insertContact(contact.toContactEntity())
    }

    override fun findById(id: Long): Contact? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<Contact> {
        TODO("Not yet implemented")
    }

    override suspend fun getAll(): Flow<List<Contact>?> =
        contactDao.getContacts()
            .map { it.map(ContactEntity::toContact) }
            .flowOn(Dispatchers.IO)

    override suspend fun get(contacts: List<String>?): Flow<List<Contact>?> =
        contactDao.getContacts(contacts.orEmpty())
            .map { it.map(ContactEntity::toContact) }
            .flowOn(Dispatchers.IO)

    override suspend fun syncContacts() {
        contactDao.getUnSyncedContacts().collect{unSyncedContacts ->
            unSyncedContacts.forEach {entity ->
                if (entity.sId.orEmpty().isEmpty()){
                    networkDataSource.syncContact(entity.toContact()).collect{

                        if (it is Result.Success){
                            if (it.data?.id.orEmpty().isNotEmpty()){
                                contactDao.synced(entity.phoneNumber, it.data?.id.orEmpty())
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun updateWithServer() {
        networkDataSource.serverContacts().collect{
            if (it is Result.Success){
                val contacts = it.data?.data?.map { ContactEntity(fullName = it.fullName.orEmpty(), phoneNumber = it.phoneNumber.orEmpty(), email = it.email, address = it.address, note = it.note) }
                contactDao.upsertAll(contacts.orEmpty())
            }
        }
    }

}