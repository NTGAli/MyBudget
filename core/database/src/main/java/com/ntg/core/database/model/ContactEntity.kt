package com.ntg.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ntg.core.model.Contact

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = false)
    val phoneNumber: String,
    val fullName: String,
    val email: String? = null,
    val note: String? = null,
    val address: String?= null,
    var sId: String? = null,
    val isSynced: Boolean = false,
)


fun ContactEntity.toContact() = Contact(
    fullName = fullName,
    phoneNumber = phoneNumber,
)


fun Contact.toContactEntity() = ContactEntity(
    fullName = fullName.orEmpty(),
    phoneNumber = phoneNumber.orEmpty(),

)