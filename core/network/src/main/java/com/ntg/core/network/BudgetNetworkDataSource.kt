package com.ntg.core.network

import com.ntg.core.network.model.ResponseBody
import com.ntg.core.network.model.Result
import kotlinx.coroutines.flow.Flow

interface BudgetNetworkDataSource {
    suspend fun loginWithPhone(phone: String): Flow<Result<ResponseBody<String?>>>
}