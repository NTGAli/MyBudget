package com.ntg.mybudget.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.nt.com.core.datastore.ThemeStatus
import com.nt.com.core.datastore.UserPreferences
import com.nt.com.core.datastore.copy
import com.ntg.core.model.ThemeState
import com.ntg.core.model.UserData
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class BudgetPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    val userData = userPreferences.data
        .map {
            UserData(
                isLogged = it.isLogged,
                token = it.token,
                expire = it.expire,
                email = it.email,
                name = it.name,
                phone = it.phone,
                avatarImage = it.avatarImage,
                themeState = it.themeStatus.toThemeState()
            )
        }

    suspend fun setUserLogged(token: String, expire: String) {
        try {
            userPreferences.updateData {
                it.copy {
                    isLogged = true
                    this.token = token
                    this.expire = expire
                    this.name = ""
                    this.email = ""
                }
            }
        } catch (ioException: IOException) {
            Log.e("BudgetPreferences", "Failed to save user login in preferences", ioException)
        }
    }

    suspend fun setLogout() {
        try {
            userPreferences.updateData {
                it.copy {
                    isLogged = false
                    token = ""
                    expire = ""
                    name = ""
                    email = ""
                    phone = ""
                    avatarImage = ""

                }
            }
        } catch (ioException: IOException) {
            Log.e("BudgetPreferences", "Failed to save user logout in preferences", ioException)
        }
    }


    suspend fun saveBasicUserData(
        name: String,
        email: String,
        phone: String,
        image: String
    ){
        userPreferences.updateData {
            it.copy {
                this.name = name
                this.email = email
                this.phone = phone
                this.avatarImage = image
            }
        }
    }

    suspend fun saveThemeChange(themeStatus: ThemeStatus) {
        userPreferences.updateData {
            it.copy {
                this.themeStatus = themeStatus
            }
        }
    }

    private fun ThemeStatus.toThemeState(): ThemeState = when (this) {
        ThemeStatus.Default -> ThemeState.Default
        ThemeStatus.Light -> ThemeState.Light
        ThemeStatus.Dark -> ThemeState.Dark
        else -> ThemeState.Default // Fallback for unknown values
    }
}

//private fun UserPreferencesKt.Dsl.updateShouldHideOnboardingIfNecessary() {
//    if (followedTopicIds.isEmpty() && followedAuthorIds.isEmpty()) {
//        shouldHideOnboarding = false
//    }
//}
