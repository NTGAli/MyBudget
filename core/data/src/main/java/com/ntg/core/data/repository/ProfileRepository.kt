package com.ntg.core.data.repository

import android.graphics.Bitmap
import com.ntg.core.model.res.UploadAvatarRes
import com.ntg.core.model.res.UserInfo
import com.ntg.core.network.model.Result
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {

    suspend fun getUserInfo(): Flow<Result<UserInfo>>

    suspend fun uploadAvatar(image: Bitmap, mimeType: String): Flow<Result<UploadAvatarRes>>
}