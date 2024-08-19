package com.ntg.core.data.repository

import com.ntg.core.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    val userData: Flow<UserData>

    suspend fun setUserLogged()

}