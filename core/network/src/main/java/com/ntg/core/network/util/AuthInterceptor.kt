package com.ntg.core.network.util

import android.content.SharedPreferences
import com.ntg.core.mybudget.common.Constants
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sharedPreferences: SharedPreferences) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val accessToken = sharedPreferences.getString(Constants.Prefs.ACCESS_TOKEN, null)


        val requestBuilder = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Accept", "application/json")

        val request = requestBuilder.build()

        return try {
            chain.proceed(request)
        } catch (e: Exception) {
            chain.proceed(originalRequest)
        }

    }

}
