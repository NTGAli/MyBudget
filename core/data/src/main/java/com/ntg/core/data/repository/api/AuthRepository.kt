package com.ntg.core.data.repository.api

import com.ntg.core.network.model.ResponseBody
import com.ntg.core.network.model.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun sendCode(phone: String): Flow<Result<ResponseBody<String?>>>

}