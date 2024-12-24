package com.ntg.core.model.res

import com.google.gson.annotations.SerializedName

data class ServerContacts(
    @SerializedName("current_page")
    val currentPage: Int = 1,
    val data: List<SyncContactsRes>?,
)
