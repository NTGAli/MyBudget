package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.core.model.Person

@Entity(
    tableName = "people"
)
data class PersonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val contactId: String,
    val transactionId: Int,
)


fun PersonEntity.toPerson() =
    Person(
        id = id,
        contactId = contactId,
        transactionId = transactionId
    )

fun Person.toEntity() =
    PersonEntity(
        id = id,
        contactId = contactId,
        transactionId = transactionId
    )