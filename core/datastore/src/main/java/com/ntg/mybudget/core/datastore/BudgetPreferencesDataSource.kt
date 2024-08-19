package com.ntg.mybudget.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.nt.com.core.datastore.UserPreferences
import com.nt.com.core.datastore.copy
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
                isLogged = it.isLogged
            )
        }

    suspend fun setUserLogged() {
        try {
            userPreferences.updateData {
                it.copy {
                    isLogged = true
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
                }
            }
        } catch (ioException: IOException) {
            Log.e("BudgetPreferences", "Failed to save user logout in preferences", ioException)
        }
    }

}

//private fun UserPreferencesKt.Dsl.updateShouldHideOnboardingIfNecessary() {
//    if (followedTopicIds.isEmpty() && followedAuthorIds.isEmpty()) {
//        shouldHideOnboarding = false
//    }
//}
