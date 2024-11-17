package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.core.model.Contact

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val phoneNumber: String,
    val transactionId: Int,
    val isSynced: Boolean = false,
)


fun ContactEntity.toContact() = Contact(
    fullName = fullName,
    phoneNumber = phoneNumber,
)


fun Contact.toContactEntity(transactionId: Int) = ContactEntity(
    fullName = fullName.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),
    transactionId = transactionId,

)