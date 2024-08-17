package com.ntg.core.data.repository

import com.ntg.core.model.Account
import com.ntg.core.model.AccountWithSources
import kotlinx.coroutines.flow.Flow


interface AccountRepository {

    suspend fun insert(account: Account)

    suspend fun delete(account: Account)

    suspend fun upsert(account: Account)

    suspend fun update(account: Account)

    fun getAll(): Flow<List<Account?>>

    fun getAccountByAccount(id: Int): Flow<Account?>

    fun getAccountsWithSources(): Flow<List<AccountWithSources>>

    fun currentAccount(): Flow<Account?>
}