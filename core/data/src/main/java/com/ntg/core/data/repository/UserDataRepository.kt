package com.ntg.core.data.repository

import com.ntg.core.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataRepository {

    val userData: Flow<UserData>

    suspend fun setUserLogged(token: String, expire: String)

    suspend fun logout()

    suspend fun saveUserBasicData(name: String, email: String, phone: String, image: String)
}