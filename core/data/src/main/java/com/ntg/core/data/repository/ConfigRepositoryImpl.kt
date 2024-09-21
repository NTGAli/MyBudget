package com.ntg.core.data.repository

import com.ntg.core.database.dao.ConfigDao
import com.ntg.core.database.model.toConfig
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.res.ServerConfig
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import com.ntg.core.network.BudgetNetworkDataSource
import com.ntg.core.network.model.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ConfigRepositoryImpl @Inject constructor(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val configDao: ConfigDao,
    private val network: BudgetNetworkDataSource
) : ConfigRepository {

    override suspend fun updateConfigs() {
        network.serverConfig().collect {
            if (it is Result.Success) {
                configDao.upsert(it.data.orEmpty().map { it.toEntity() })
            }
        }
    }

    override fun get(): Flow<List<ServerConfig>> =
        flow {
            emit(
                configDao.getAll()
                    .map { it.toConfig() }
            )
        }
            .flowOn(ioDispatcher)

    override fun get(key: String): Flow<ServerConfig?> =
        flow {
            emit(
                configDao.get(key)
                    ?.toConfig()
            )
        }
            .flowOn(ioDispatcher)
}