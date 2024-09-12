package com.ntg.core.data.repository

import android.content.SharedPreferences
import com.ntg.core.model.UserData
import com.ntg.mybudget.core.datastore.BudgetPreferencesDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class MainUserDataRepository @Inject constructor(
    private val budgetPreferencesDataSource: BudgetPreferencesDataSource,
    private val sharedPreferences: SharedPreferences
) : UserDataRepository{

    override val userData: Flow<UserData> =
        budgetPreferencesDataSource.userData

    override suspend fun setUserLogged(token: String, expire: String) {
        budgetPreferencesDataSource.setUserLogged(token, expire)
    }

    override suspend fun logout() {
        sharedPreferences.edit().clear().apply()
        budgetPreferencesDataSource.setLogout()
    }
}