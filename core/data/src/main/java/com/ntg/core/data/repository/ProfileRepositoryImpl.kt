package com.ntg.core.data.repository

import com.ntg.core.model.res.UserInfo
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.model.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val network: BudgetNetworkDataSource,
) : ProfileRepository {
    override suspend fun getUserInfo(): Flow<Result<UserInfo>> {
        return network.getUser()
    }
}