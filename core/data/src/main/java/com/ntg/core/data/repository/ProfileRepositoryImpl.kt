package com.ntg.core.data.repository

import android.graphics.Bitmap
import com.ntg.core.model.res.SessionsResItem
import com.ntg.core.model.res.UploadAvatarRes
import com.ntg.core.model.res.UserInfo
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.model.ResponseBody
import com.ntg.core.network.model.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val network: BudgetNetworkDataSource,
) : ProfileRepository {
    override suspend fun getUserInfo(): Flow<Result<UserInfo>> {
        return network.getUser()
    }

    override suspend fun uploadAvatar(image: Bitmap, mimeType: String): Flow<Result<UploadAvatarRes>> {
        return network.uploadAvatar(image, mimeType)
    }

    override suspend fun updateUserInfo(name: String, username: String): Flow<Result<ResponseBody<String?>>> {
        return network.updateUserInfo(name, username)
    }

    override suspend fun getSessionsList(): Flow<Result<List<SessionsResItem>>> {
        return network.getSessionsList()
    }

    override suspend fun terminateAllSessions(): Flow<Result<ResponseBody<String?>>> {
        return network.terminateAllSessions()
    }

    override suspend fun terminateSession(sessionId: String): Flow<Result<ResponseBody<String?>>> {
        return network.terminateSession(sessionId)
    }

}