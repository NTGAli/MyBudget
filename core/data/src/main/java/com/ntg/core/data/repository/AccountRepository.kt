package com.ntg.core.data.repository

import com.ntg.core.model.Account
import com.ntg.core.model.AccountWithSources
import com.ntg.core.model.res.WalletType
import kotlinx.coroutines.flow.Flow


interface AccountRepository {

    suspend fun insert(account: Account)

    suspend fun delete(account: Account)

    suspend fun delete(accountId: Int)

    suspend fun upsert(account: Account)

    suspend fun update(account: Account)

    fun getAll(): Flow<List<Account?>>

    fun getAccountByAccount(id: Int): Flow<Account?>

    fun getAccountsWithSources(): Flow<List<AccountWithSources>>

    fun currentAccount(): Flow<Account?>

    fun getUnSyncedAccounts(): Flow<List<Account>?>

    fun getRemovedAccounts(): Flow<List<Account>?>

    suspend fun synced(id: Int, sId: String)

    //server
    suspend fun syncAccounts()

    suspend fun walletTypes(): Flow<List<WalletType>?>


}