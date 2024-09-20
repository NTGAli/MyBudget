package com.ntg.core.network

import com.ntg.core.model.req.VerifyOtp
import com.ntg.core.model.res.CodeVerification
import com.ntg.core.model.res.ServerAccount
import com.ntg.core.model.res.ServerConfig
import com.ntg.core.model.res.SyncedAccount
import com.ntg.core.model.res.WalletType
import com.ntg.core.network.model.ResponseBody
import com.ntg.core.network.model.Result
import kotlinx.coroutines.flow.Flow

interface BudgetNetworkDataSource {
    suspend fun loginWithPhone(phone: String): Flow<Result<ResponseBody<String?>>>

    suspend fun verifyCode(code: VerifyOtp): Flow<Result<CodeVerification?>>

    suspend fun serverAccount(): Flow<Result<List<ServerAccount>?>>

    suspend fun syncAccount(name: String): Flow<Result<SyncedAccount?>>

    suspend fun updateAccount(name: String, id: String): Flow<Result<SyncedAccount?>>

    suspend fun walletTypes(): Flow<Result<List<WalletType>?>>

    suspend fun removeAccount(id: String): Flow<Result<ResponseBody<String?>>>

    suspend fun serverConfig(): Flow<Result<List<ServerConfig>?>>
}