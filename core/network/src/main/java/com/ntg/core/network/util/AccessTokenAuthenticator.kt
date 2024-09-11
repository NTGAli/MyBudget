//package com.ntg.core.network.util
//
//import android.content.SharedPreferences
//import androidx.core.content.edit
//import androidx.datastore.core.DataStore
//import androidx.lifecycle.asLiveData
//import com.nt.com.core.datastore.UserPreferences
//import kotlinx.coroutines.runBlocking
//import okhttp3.Authenticator
//import okhttp3.Request
//import okhttp3.Response
//import okhttp3.Route
//
//
//class AccessTokenAuthenticator(
//    private val userPreferences: DataStore<UserPreferences>
//) : Authenticator {
//
//    override fun authenticate(route: Route?, response: Response): Request? {
//
//        userPreferences.data.collect{
//
//            val accessToken = it.token
//
//
//        }
//
////        val accessToken = sharedPreferences.getString(Constants.Prefs.ACCESS_TOKEN, null)
//
////        Timber.d("authenticate")
////        Timber.d("authenticate accessToken %s ", accessToken)
//
//        if (!isRequestWithAccessToken(response) || accessToken.isNullOrEmpty()) {
////            Timber.d("authenticate accessToken is null")
//            return null
//        }
//        runBlocking {
//
//            userPreferences.data.collect{
//
//                val accessToken = it.token
//
//
//            }
//
//        }
//
//
//        synchronized(this) {
//
//            Timber.d("authenticate synchronized enter!")
//
//            val newAccessToken = sharedPreferences.getString(Constants.Prefs.ACCESS_TOKEN, null)
//
//            Timber.d(
//                "authenticate newAccessToken %s %s",
//                newAccessToken,
//                newAccessToken == accessToken
//            )
//
//            // Access token is refreshed in another thread.
//            if (newAccessToken != accessToken && accessToken.isNotEmpty() && newAccessToken?.isNotEmpty() == true) {
//                return newRequestWithAccessToken(response.request, newAccessToken)
//            }
//
//            // Need to refresh an access token
//            val updatedAccessToken = refreshAccessToken()
//            Timber.d("authenticate updatedAccessToken %s ", updatedAccessToken)
//            Timber.d("authenticate synchronized leave!")
//            return newRequestWithAccessToken(response.request, updatedAccessToken)
//        }
//    }
//
//    private fun isRequestWithAccessToken(response: Response): Boolean {
//        val header = response.request.header("Authorization")
//        return header != null && header.startsWith("Bearer")
//    }
//
//    private fun newRequestWithAccessToken(request: Request, accessToken: String?): Request? {
//        if (accessToken.isNullOrEmpty()) {
//            return null
//        }
//        return request.newBuilder()
//            .header("Authorization", "Bearer $accessToken")
//            .build()
//    }
//
//    private fun refreshAccessToken(): String? {
//        Timber.d("authenticate enter refreshAccessToken")
//
//        val refreshToken = sharedPreferences.getString(Constants.Prefs.REFRESH_TOKEN, null)
//        val employeeId = sharedPreferences.getString(Constants.Prefs.EMPLOYEE_ID , "")
//
//        if (!refreshToken.isNullOrEmpty()) {
//            val response = tokenService.refreshAccessToken(refreshToken , employeeId.orDefault()).execute()
//            Timber.d("authenticate refreshAccessToken %s ", response)
//            return if (response.isSuccessful) {
//                Timber.d("GetResponse::SUCCESS")
//                saveToken(response.body()?.data)
//            } else {
//                Timber.d("GetResponse::FAIL::LOGOUT")
//                MainActivity.userLogout.postValue(true)
//                null
//            }
//        }
//        return null
//    }
//
//    private fun saveToken(signInRes: SignInRes?): String? {
//
//        if (signInRes != null) {
//
//            accountsDao.updateAccount(
//                sharedPreferences.getString(Constants.Prefs.PHONE, "").toString(),
//                signInRes.accessToken,
//                signInRes.refreshToken,
//                signInRes.accessTokenExpireOnUnix,
//                signInRes.refreshTokenExpireOnUnix
//            )
//
//            sharedPreferences.edit {
//                putString(Constants.Prefs.ACCESS_TOKEN, signInRes.accessToken)
//                putString(Constants.Prefs.REFRESH_TOKEN, signInRes.refreshToken)
//                putLong(Constants.Prefs.ACCESS_TOKEN_EXPIRE, signInRes.accessTokenExpireOnUnix)
//                putLong(Constants.Prefs.REFRESH_TOKEN_EXPIRE, signInRes.refreshTokenExpireOnUnix)
//            }
//
//            return signInRes.accessToken
//        }
//
//        return null
//
//    }
//}
