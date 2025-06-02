package com.ntg.core.data.repository

import com.ntg.core.database.dao.WalletsDao
import com.ntg.core.database.model.WalletEntity
import com.ntg.core.database.model.asWallet
import com.ntg.core.database.model.toCurrency
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.Wallet
import com.ntg.core.model.SourceWithDetail
import com.ntg.core.model.res.Currency
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.mybudget.common.orFalse
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WalletsRepositoryImpl @Inject constructor(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val walletDao: WalletsDao,
    private val network: BudgetNetworkDataSource
) : WalletsRepository {

    override suspend fun insert(wallet: Wallet) {
        walletDao.insert(wallet.toEntity())
    }

    override suspend fun update(wallet: Wallet) {
        walletDao.update(wallet.toEntity())
    }

    override suspend fun delete(sourceExpenditure: Wallet) {
        walletDao.delete(sourceExpenditure.toEntity())
    }

    override suspend fun tempRemove(sourceId: Int) {
        walletDao.tempRemove(sourceId)
    }

    override suspend fun updateSelectedSources(sourceIds: List<Int>) {
        if (sourceIds.isNotEmpty()) {
            walletDao.updateSelectedSources(sourceIds)
        }
    }

    override suspend fun selectWalletFronDefault() {
        walletDao.selectDefaultWallet()
    }

    override suspend fun selectFirstWallet() {
        walletDao.selectFirstWallet()
    }

    override fun getAll(): Flow<List<Wallet>> =
        flow {
            emit(
                walletDao.getAll()
                    .map(WalletEntity::asWallet)
            )
        }
            .flowOn(ioDispatcher)

    override fun getSourcesByAccount(accountId: Int): Flow<List<SourceWithDetail>> =
        flow {
            emit(
                walletDao.getSourcesWithDetails(accountId)
            )
        }.flowOn(ioDispatcher)

    override fun getSourcesById(accountId: Int): Flow<Wallet?> =
        flow {
            emit(
                walletDao.getSource(accountId)
                    ?.asWallet()
            )
        }.flowOn(ioDispatcher)

    override fun getSourceDetails(id: Int): Flow<Wallet?> =
        flow {
            emit(
                walletDao.getSource(id)?.asWallet()
            )
        }.flowOn(ioDispatcher)

    override suspend fun getSelectedWalletIds(): Flow<List<Int>> =
        flow {
            emit(
                walletDao.getSelectedWalletIds()
            )
        }.flowOn(ioDispatcher)


    override suspend fun getSelectedSources(): Flow<List<Wallet>> =
        flow {
            emit(
                walletDao.getSelectedSources().map {
                    it.asWallet()
                }
            )
        }.flowOn(ioDispatcher)

    override suspend fun getCurrentCurrency(): Flow<Currency?> =
        flow {
            emit(
                walletDao.currentCurrency()?.toCurrency()
            )
        }.flowOn(ioDispatcher)

    override suspend fun getCurrentCurrency(accountId: Int): Flow<Currency?> =
        flow {
            emit(
                walletDao.currentCurrency(accountId)?.toCurrency()
            )
        }.flowOn(ioDispatcher)


    override suspend fun syncSources() {
        walletDao.unSynced().collect {
            it.forEach { row ->
                val sourceType = row.data

                if (row.isRemoved.orFalse()) {
                    if (row.sId == null) {
                        // wallet not synced yet
                        walletDao.forceDelete(row.id)
                        if (row.type == 1){
                            walletDao.forceDelete(row.id)
                        }
                    } else {
                        // wallet already sync, wait server remove
                        network.removeWallet(row.sId!!).collect {
                            if (it is Result.Success) {
                                if (row.type == 1){
                                    walletDao.forceDelete(row.id)
                                }
                            }
                        }
                    }

                } else if (row.sId == null) {
                    //create new wallet on server
                    network.syncSources(
                        SourceWithDetail(
                            id = row.id,
                            accountId = row.accountId,
                            accountSId = row.accountSId,
                            currencyId = row.currencyId,
                            type = row.type ?: -1,
                            sourceType = sourceType,
                        )
                    ).collect { result ->

                        if (result is Result.Success) {
                            if (result.data != null) {
                                walletDao.sync(row.id, result.data?.id.orEmpty())
                            }
                        }
                    }
                }else{
                    //update wallet
                    network.updateWallet(
                        row.sId.orEmpty(),
                        SourceWithDetail(
                            id = row.id,
                            accountId = row.accountId,
                            accountSId = row.accountSId,
                            currencyId = row.currencyId,
                            type = row.type ?: -1,
                            sourceType = sourceType,
                        )
                    ).collect { result ->
                        if (result is Result.Success) {
                            if (result.data != null) {
                                walletDao.sync(row.id, result.data?.id.orEmpty())
                            }
                        }
                    }
                }


            }
        }
    }

    override suspend fun needToSync(id: Int) {
        walletDao.unSync(id)
    }

}