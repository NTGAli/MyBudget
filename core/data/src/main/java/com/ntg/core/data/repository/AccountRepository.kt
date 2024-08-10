package com.ntg.core.data.repository

import com.ntg.core.model.Account
import kotlinx.coroutines.flow.Flow


interface AccountRepository {

    suspend fun insert(account: Account)

    suspend fun delete(account: Account)

    fun getAll(): Flow<List<Account?>>

    fun getAccount(id: Int): Flow<Account?>

    fun currentAccount(): Flow<Account?>
}