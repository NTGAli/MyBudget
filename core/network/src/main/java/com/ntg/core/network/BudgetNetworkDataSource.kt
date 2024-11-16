package com.ntg.core.network

import android.graphics.Bitmap
import com.ntg.core.model.SourceWithDetail
import com.ntg.core.model.req.VerifyOtp
import com.ntg.core.model.res.Bank
import com.ntg.core.model.res.CategoryRes
import com.ntg.core.model.res.CodeVerification
import com.ntg.core.model.res.Currency
import com.ntg.core.model.res.ServerAccount
import com.ntg.core.model.res.ServerConfig
import com.ntg.core.model.res.SessionsResItem
import com.ntg.core.model.res.SyncedAccount
import com.ntg.core.model.res.SyncedWallet
import com.ntg.core.model.res.UploadAvatarRes
import com.ntg.core.model.res.UserInfo
import com.ntg.core.model.res.WalletType
import com.ntg.core.network.model.ResponseBody
import com.ntg.core.network.model.Result
import kotlinx.coroutines.flow.Flow
import java.io.File

interface BudgetNetworkDataSource {
    suspend fun loginWithPhone(phone: String): Flow<Result<ResponseBody<String?>>>

    suspend fun verifyCode(code: VerifyOtp): Flow<Result<CodeVerification?>>

    suspend fun serverAccount(): Flow<Result<List<ServerAccount>?>>

    suspend fun syncAccount(name: String): Flow<Result<SyncedAccount?>>

    suspend fun updateAccount(name: String, id: String): Flow<Result<SyncedAccount?>>

    suspend fun walletTypes(): Flow<Result<List<WalletType>?>>

    suspend fun removeAccount(id: String): Flow<Result<ResponseBody<String?>>>

    suspend fun serverConfig(): Flow<Result<List<ServerConfig>?>>

    suspend fun serverBanks(): Flow<Result<List<Bank>?>>

    suspend fun currencies(): Flow<Result<List<Currency>?>>

    suspend fun syncSources(source: SourceWithDetail): Flow<Result<SyncedAccount?>>

    suspend fun removeWallet(id: String): Flow<Result<ResponseBody<String?>>>

    suspend fun updateWallet(id: String, source: SourceWithDetail): Flow<Result<SyncedWallet?>>

    suspend fun categories(): Flow<Result<CategoryRes?>>

    suspend fun getUser(): Flow<Result<UserInfo>>

    suspend fun uploadAvatar(image: Bitmap, mimeType: String): Flow<Result<UploadAvatarRes>>

    suspend fun updateUserInfo(name: String, username: String): Flow<Result<ResponseBody<String?>>>

    suspend fun getSessionsList(): Flow<Result<List<SessionsResItem>>>

    suspend fun terminateAllSessions(): Flow<Result<ResponseBody<String?>>>

    suspend fun terminateSession(sessionId: String): Flow<Result<ResponseBody<String?>>>
}