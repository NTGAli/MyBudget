package com.ntg.core.network

import com.ntg.core.model.req.VerifyOtp
import com.ntg.core.model.res.CodeVerification
import com.ntg.core.network.model.ResponseBody
import com.ntg.core.network.model.Result
import kotlinx.coroutines.flow.Flow

interface BudgetNetworkDataSource {
    suspend fun loginWithPhone(phone: String): Flow<Result<ResponseBody<String?>>>

    suspend fun verifyCode(code: VerifyOtp): Flow<Result<CodeVerification?>>
}