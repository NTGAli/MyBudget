package com.ntg.core.data.repository

import android.content.SharedPreferences
import com.nt.com.core.datastore.ThemeStatus
import com.ntg.core.model.ThemeState
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

    override suspend fun saveUserBasicData(
        name: String,
        email: String,
        phone: String,
        image: String
    ) {
        budgetPreferencesDataSource.saveBasicUserData(name, email, phone, image)
    }

    override suspend fun changeTheme(themeState: ThemeState) {
        budgetPreferencesDataSource.saveThemeChange(themeState.toThemeStatus())
    }

    private fun ThemeState.toThemeStatus(): ThemeStatus = when (this) {
        ThemeState.Default -> ThemeStatus.Default
        ThemeState.Light -> ThemeStatus.Light
        ThemeState.Dark -> ThemeStatus.Dark
    }
}