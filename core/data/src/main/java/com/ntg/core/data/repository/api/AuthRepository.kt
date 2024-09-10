package com.ntg.core.data.repository.api

import com.ntg.core.model.req.VerifyOtp
import com.ntg.core.model.res.CodeVerification
import com.ntg.core.network.model.ResponseBody
import com.ntg.core.network.model.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    suspend fun sendCode(phone: String): Flow<Result<ResponseBody<String?>>>

    suspend fun verifyCode(verification: VerifyOtp): Flow<Result<CodeVerification?>>

}