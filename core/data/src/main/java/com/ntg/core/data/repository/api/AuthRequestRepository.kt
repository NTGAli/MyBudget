package com.ntg.core.data.repository.api

import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.model.ResponseBody
import com.ntg.core.network.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class AuthRequestRepository @Inject constructor(
    private val network: BudgetNetworkDataSource,
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
): AuthRepository {
    override suspend fun sendCode(phone: String): Flow<Result<ResponseBody<String?>>> {
        return network.loginWithPhone(phone)
    }
}