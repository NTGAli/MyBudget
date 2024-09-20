package com.ntg.core.network.retrofit

import com.ntg.core.model.req.VerifyOtp
import com.ntg.core.model.res.CodeVerification
import com.ntg.core.model.res.ServerAccount
import com.ntg.core.model.res.ServerConfig
import com.ntg.core.model.res.SyncedAccount
import com.ntg.core.model.res.WalletType
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

    override suspend fun serverAccount(): Flow<Result<List<ServerAccount>?>> {
        return networkBoundResources.downloadData(ioDispatcher){
            apiService.my()
        }
    }

    override suspend fun syncAccount(name: String): Flow<Result<SyncedAccount?>> {
        return networkBoundResources.downloadData(ioDispatcher){
            apiService.syncAccount(name)
        }
    }

    override suspend fun updateAccount(name: String, id: String): Flow<Result<SyncedAccount?>> {
        return networkBoundResources.downloadData(ioDispatcher){
            apiService.updateAccount(name = name, id = id)
        }
    }

    override suspend fun walletTypes(): Flow<Result<List<WalletType>?>> {
        return networkBoundResources.downloadData(ioDispatcher){
            apiService.walletTypes()
        }
    }


    override suspend fun removeAccount(id: String): Flow<Result<ResponseBody<String?>>> {
        return networkBoundResources.downloadData(ioDispatcher){
            apiService.deleteAccount(id)
        }
    }

    override suspend fun serverConfig(): Flow<Result<List<ServerConfig>?>> {
        return networkBoundResources.downloadData(ioDispatcher){
            apiService.configs()
        }
    }
}
