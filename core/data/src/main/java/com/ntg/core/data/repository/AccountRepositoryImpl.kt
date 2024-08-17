package com.ntg.core.data.repository

import android.util.Log
import com.ntg.core.database.dao.AccountDao
import com.ntg.core.database.model.AccountEntity
import com.ntg.core.database.model.asAccount
import com.ntg.core.database.model.toEntity
import com.ntg.core.model.Account
import com.ntg.core.model.AccountWithSources
import com.ntg.core.mybudget.common.BudgetDispatchers
import com.ntg.core.mybudget.common.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    @Dispatcher(BudgetDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
    private val accountDao: AccountDao
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


    override fun getAccount(id: Int): Flow<Account?> {
        Log.d("wdawd", "wadawd $id")
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
}