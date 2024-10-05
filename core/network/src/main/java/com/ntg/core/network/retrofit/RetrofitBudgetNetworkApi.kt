package com.ntg.core.network.retrofit

import com.google.gson.Gson
import com.ntg.core.model.SourceType
import com.ntg.core.model.SourceWithDetail
import com.ntg.core.model.req.SourceDetailReq
import com.ntg.core.model.req.VerifyOtp
import com.ntg.core.model.res.Bank
import com.ntg.core.model.res.BankRes
import com.ntg.core.model.res.CodeVerification
import com.ntg.core.model.res.Currency
import com.ntg.core.model.res.ServerAccount
import com.ntg.core.model.res.ServerConfig
import com.ntg.core.model.res.SyncedAccount
import com.ntg.core.model.res.SyncedWallet
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

    override suspend fun serverBanks(): Flow<Result<List<Bank>?>> {
        return networkBoundResources.downloadData(ioDispatcher){
            apiService.banks()
        }
    }

    override suspend fun currencies(): Flow<Result<List<Currency>?>> {
        return networkBoundResources.downloadData(ioDispatcher){
            apiService.currencies()
        }
    }

    override suspend fun syncSources(source: SourceWithDetail): Flow<Result<SyncedAccount?>> {
        return networkBoundResources.downloadData(ioDispatcher){
            val details = if (source.sourceType is SourceType.BankCard){
                Gson().toJson(SourceDetailReq(
                    cart_number = (source.sourceType as SourceType.BankCard).number,
                    bank_id = (source.sourceType as SourceType.BankCard).bankId.toString(),
                    bank_name = (source.sourceType as SourceType.BankCard).nativeName,
                    cart_owner_name = (source.sourceType as SourceType.BankCard).name,
                    cvv2 = (source.sourceType as SourceType.BankCard).cvv
                ))
            }else ""
            apiService.syncWallet(
                walletType = source.type.toString(),
                currencyId = source.currencyId.toString(),
                accountId = source.accountSId.toString(),
                details = details
            )
        }
    }

    override suspend fun removeWallet(id: String): Flow<Result<ResponseBody<String?>>> {
        return networkBoundResources.downloadData(ioDispatcher){
            apiService.deleteWallet(id)
        }
    }

    override suspend fun updateWallet(id: String, source: SourceWithDetail): Flow<Result<SyncedWallet?>> {
        return networkBoundResources.downloadData(ioDispatcher){
            val details = if (source.sourceType is SourceType.BankCard){
                Gson().toJson(SourceDetailReq(
                    cart_number = (source.sourceType as SourceType.BankCard).number,
                    bank_id = (source.sourceType as SourceType.BankCard).bankId.toString(),
                    bank_name = (source.sourceType as SourceType.BankCard).nativeName,
                    cart_owner_name = (source.sourceType as SourceType.BankCard).name,
                    cvv2 = (source.sourceType as SourceType.BankCard).cvv
                ))
            }else ""
            apiService.updateWallet(id, details)
        }
    }
}
