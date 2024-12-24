package com.ntg.core.data.repository

import com.ntg.core.database.model.PersonEntity
import com.ntg.core.model.res.Category
import kotlinx.coroutines.flow.Flow

interface PeopleRepository {

    suspend fun insert(person: PersonEntity)

    suspend fun insertAll(people: List<PersonEntity>)

    suspend fun delete(person: PersonEntity)

}