package com.ntg.core.data.repository

import com.ntg.core.database.dao.AccountDao
import com.ntg.core.database.dao.WalletDao
import com.ntg.core.database.model.AccountEntity
import com.ntg.core.database.model.WalletTypeEntity
import com.ntg.core.database.model.asAccount
import com.ntg.core.database.model.asWalletType
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.Account
import com.ntg.core.model.AccountWithSources
import com.ntg.core.model.res.WalletType
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val accountDao: AccountDao,
    private val network: BudgetNetworkDataSource,
    private val walletDao: WalletDao
    ): AccountRepository {
    override suspend fun insert(account: Account) {
        accountDao.insert(account.toEntity())
    }

    override suspend fun delete(account: Account) {
        accountDao.delete(account.toEntity())
    }

    override suspend fun upsert(account: Account) {
        accountDao.upsert(account.toEntity())
    }

    override suspend fun update(account: Account) {
        accountDao.insert(account.toEntity())
    }

    override fun getAll(): Flow<List<Account?>> =
        flow {
            emit(
                accountDao.getAll()
                    .map(AccountEntity::asAccount)
            )
        }
            .flowOn(ioDispatcher)


    override fun getAccountByAccount(id: Int): Flow<Account?> {
        return flow {
            emit(
                accountDao.getAccount(id)?.asAccount()
            )
        }
            .flowOn(ioDispatcher)
    }



    override fun getAccountsWithSources(): Flow<List<AccountWithSources>> =
        flow {
            emit(
                accountDao.getAccountBySources()
            )
        }
            .flowOn(ioDispatcher)

    override fun currentAccount(): Flow<Account?> =
        flow {
            emit(
                accountDao.currentAccount()
                    ?.asAccount()
            )
        }
            .flowOn(ioDispatcher)

    override fun getUnSyncedAccounts(): Flow<List<Account>?>  =
        flow {
            emit(
                accountDao.unSynced()
                    ?.map(AccountEntity::asAccount)
            )
        }
            .flowOn(ioDispatcher)

    override suspend fun synced(id: Int, sId: String) {
        accountDao.synced(id, sId)
    }

    override suspend fun syncAccounts() {
        getUnSyncedAccounts().collect{
            it?.forEach { account ->
                if (account.name.isNotEmpty()){
                    if (account.sId.orEmpty().isNotEmpty()){
                        //update synced account
                        network.updateAccount(account.name).collect{
                            if (it is Result.Success){
                                if (it.data?.id.orEmpty().isNotEmpty()){
                                    synced(account.id, it.data?.id.orEmpty())
                                }
                            }
                        }
                    }else{
                        // sync account
                        network.syncAccount(account.name).collect{
                            if (it is Result.Success){
                                if (it.data?.id.orEmpty().isNotEmpty()){
                                    synced(account.id, it.data?.id.orEmpty())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun walletTypes(): Flow<List<WalletType>?> {
        network.walletTypes().collect{
            if (it is Result.Success){
                if (it.data.orEmpty().isNotEmpty()){
                    walletDao.deleteAll()
                }
                walletDao.upsert(it.data.orEmpty().map { it.toEntity() })
            }
        }
        return flow {
            emit(
                walletDao.walletTypes()
                    ?.map(WalletTypeEntity::asWalletType)
            )
        }
            .flowOn(ioDispatcher)
    }
}