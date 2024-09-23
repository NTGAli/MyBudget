package com.ntg.core.data.repository

import android.util.Log
import com.ntg.core.database.dao.AccountDao
import com.ntg.core.database.dao.BankCardDao
import com.ntg.core.database.dao.SourceExpenditureDao
import com.ntg.core.database.dao.TransactionsDao
import com.ntg.core.database.dao.WalletDao
import com.ntg.core.database.dao.ConfigDao
import com.ntg.core.database.model.AccountEntity
import com.ntg.core.database.model.WalletTypeEntity
import com.ntg.core.database.model.asAccount
import com.ntg.core.database.model.asWalletType
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.Account
import com.ntg.core.model.AccountWithSources
import com.ntg.core.model.SourceType
import com.ntg.core.model.SourceWithDetail
import com.ntg.core.model.res.WalletType
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val accountDao: AccountDao,
    private val sourceDao: SourceExpenditureDao,
    private val bankCardDao: BankCardDao,
    private val transactionsDao: TransactionsDao,
    private val network: BudgetNetworkDataSource,
    private val walletDao: WalletDao
) : AccountRepository {
    override suspend fun insert(account: Account) {
        accountDao.insert(account.toEntity())
    }

    override suspend fun delete(account: Account) {
        accountDao.delete(account.toEntity())
    }

    override suspend fun delete(accountId: Int) {
        accountDao.delete(accountId)
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
            accountDao.getAccountBySources().collect{rawData ->
                val accountsMap = rawData.groupBy { it.accountId }
                emit(
                    accountsMap.map { (accountId, sources) ->
                        AccountWithSources(
                            accountId = accountId,
                            accountName = sources.first().accountName,
                            isDefault = sources.first().isDefaultAccount,
                            sources = if (sources.first().sourceId != null){
                                sources.map { row ->
                                    val sourceType = when (row.type) {
                                        1 -> {
                                            if (row.number != null){
                                                SourceType.BankCard(
                                                    id = row.bankId ?: -1,
                                                    number = row.number ?: "",
                                                    cvv = row.cvv ?: "",
                                                    date = row.expire ?: "",
                                                    name = row.name.orEmpty(),
                                                    sheba = row.sheba.orEmpty(),
                                                    accountNumber = row.accountNumber.orEmpty()
                                                )
                                            }else null
                                        }

                                        2 -> SourceType.Gold(
                                            value = row.value ?: 0.0,
                                            weight = row.weight ?: 0.0
                                        )

                                        else -> null

                                    }
                                    if (sourceType != null){
                                        SourceWithDetail(
                                            id = row.sourceId ?: 0,
                                            accountId = row.accountId,
                                            type = row.type ?: 0,
                                            name = row.name ?: "",
                                            sourceType = sourceType
                                        )
                                    }else null
                                }
                            }else emptyList()
                        )
                    }
                )
            }
        }
            .flowOn(ioDispatcher)

    override fun getSelectedAccount(): Flow<List<AccountWithSources>> =
        flow {
            accountDao.getSelectedAccountBySources().collect{rawData ->
                val accountsMap = rawData.groupBy { it.accountId }
                emit(
                    accountsMap.map { (accountId, sources) ->
                        AccountWithSources(
                            accountId = accountId,
                            accountName = sources.first().accountName,
                            isDefault = sources.first().isDefaultAccount,
                            sources = if (sources.first().sourceId != null){
                                sources.map { row ->
                                    val sourceType = when (row.type) {
                                        1 -> {
                                            if (row.number != null){
                                                SourceType.BankCard(
                                                    id = row.bankId ?: -1,
                                                    number = row.number ?: "",
                                                    cvv = row.cvv ?: "",
                                                    date = row.expire ?: "",
                                                    name = row.name.orEmpty(),
                                                    sheba = row.sheba.orEmpty(),
                                                    accountNumber = row.accountNumber.orEmpty()
                                                )
                                            }else null
                                        }

                                        2 -> SourceType.Gold(
                                            value = row.value ?: 0.0,
                                            weight = row.weight ?: 0.0
                                        )

                                        else -> null

                                    }
                                    if (sourceType != null){
                                        SourceWithDetail(
                                            id = row.sourceId ?: 0,
                                            accountId = row.accountId,
                                            type = row.type ?: 0,
                                            name = row.name ?: "",
                                            sourceType = sourceType
                                        )
                                    }else null
                                }
                            }else emptyList()
                        )
                    }
                )
            }
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

    override fun getUnSyncedAccounts(): Flow<List<Account>?> =
        flow {
            emit(
                accountDao.unSynced()
                    ?.map(AccountEntity::asAccount)
            )
        }
            .flowOn(ioDispatcher)

    override fun getRemovedAccounts(): Flow<List<Account>?> =
        flow {
            emit(
                accountDao.removedAccounts()
                    ?.map(AccountEntity::asAccount)
            )
        }
            .flowOn(ioDispatcher)

    override suspend fun synced(id: Int, sId: String) {
        accountDao.synced(id, sId)
    }

    override suspend fun syncAccounts() {
        getUnSyncedAccounts().collect {
            it?.forEach { account ->
                if (account.name.isNotEmpty()) {
                    if (account.sId.orEmpty().isNotEmpty()) {
                        //update synced account
                        network.updateAccount(name = account.name, id = account.sId.orEmpty()).collect {
                            if (it is Result.Success) {
                                if (it.data?.id.orEmpty().isNotEmpty()) {
                                    synced(account.id, it.data?.id.orEmpty())
                                }
                            }
                        }
                    } else {
                        // sync account
                        network.syncAccount(account.name).collect {
                            if (it is Result.Success) {
                                if (it.data?.id.orEmpty().isNotEmpty()) {
                                    synced(account.id, it.data?.id.orEmpty())
                                }
                            }
                        }
                    }
                }
            }
        }

        getRemovedAccounts().collect {
            it?.forEach { account ->
                Log.d("removed", account.sId.toString())
                if (account.sId != null){
                    network.removeAccount(account.sId.orEmpty()).collect {
                        Log.d("removed - 2", it.toString())
                        if (it is Result.Success) {
                            Log.d("removed - 3", it.toString())
                            deleteAccountData(account.id)
                        }
                    }
                }else{
                    deleteAccountData(account.id)
                }
            }
        }
    }

    private suspend fun deleteAccountData(accountId: Int){
        accountDao.forceDelete(accountId)
        sourceDao.getSources(accountId).forEach {
            if (it.type == 1){
                bankCardDao.forceDelete(sourceId = it.id)
            }
            sourceDao.forceDelete(accountId = accountId)
            transactionsDao.deleteByAccount(accountId = accountId)
        }
    }

    override suspend fun walletTypes(): Flow<List<WalletType>?> {
        network.walletTypes().collect {
            if (it is Result.Success) {
                if (it.data.orEmpty().isNotEmpty()) {
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