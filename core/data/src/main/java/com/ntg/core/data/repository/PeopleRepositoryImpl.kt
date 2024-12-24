package com.ntg.core.data.repository

import com.ntg.core.database.dao.ContactDao
import com.ntg.core.database.dao.PersonDao
import com.ntg.core.database.model.PersonEntity
import javax.inject.Inject

class PeopleRepositoryImpl @Inject constructor(
    private val contactDao: ContactDao,
    private val peopleDao: PersonDao
) : PeopleRepository {
    override suspend fun insert(person: PersonEntity) {

    }

    override suspend fun insertAll(people: List<PersonEntity>) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(person: PersonEntity) {
        TODO("Not yet implemented")
    }
}