package com.ntg.core.network.retrofit

import com.ntg.core.model.req.VerifyOtp
import com.ntg.core.model.res.CodeVerification
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.NetworkBoundResource
import com.ntg.core.network.model.ResponseBody
import com.ntg.core.network.service.BudgetService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import com.ntg.core.network.model.Result
import javax.inject.Singleton


@Singleton
internal class RetrofitBudgetNetwork @Inject constructor(
    private val apiService: BudgetService,
    private val networkBoundResources: NetworkBoundResource,
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : BudgetNetworkDataSource {

    override suspend fun loginWithPhone(phone: String): Flow<Result<ResponseBody<String?>>> {
        return networkBoundResources.downloadData(ioDispatcher) {
            apiService.sendVerificationCode(phone)
        }
    }

    override suspend fun verifyCode(code: VerifyOtp): Flow<Result<CodeVerification?>> {
        return networkBoundResources.downloadData(ioDispatcher){
            apiService.verifyOtp(otp = code.otp, query = code.query)
        }
    }
}
