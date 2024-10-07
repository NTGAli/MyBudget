package com.ntg.core.network.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.tracing.trace
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ntg.core.network.BuildConfig
import com.ntg.core.network.service.BudgetService
import com.ntg.core.network.util.AuthInterceptor
import com.ntg.core.network.util.GzipInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }


    @Provides
    @Singleton
    fun provideLoggerInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor { message -> Log.d("HttpLoggingInterceptor", message) }
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.HEADERS }
        interceptor.apply { interceptor.level = HttpLoggingInterceptor.Level.BODY }
        return interceptor
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggerInterceptor: HttpLoggingInterceptor,
        @ApplicationContext application: Context,
        sharedPreferences: SharedPreferences,
    ): OkHttpClient {
        val timeOut = 10
        val httpClient = OkHttpClient().newBuilder()
            .protocols(listOf(Protocol.HTTP_2,  Protocol.HTTP_1_1))
            .connectTimeout(timeOut.toLong(), TimeUnit.SECONDS)
            .readTimeout(timeOut.toLong(), TimeUnit.SECONDS)
            .writeTimeout(timeOut.toLong(), TimeUnit.SECONDS)
            .addInterceptor(AuthInterceptor(sharedPreferences))
            .addInterceptor(loggingInterceptor)
//            .addInterceptor(GzipInterceptor())
            .addInterceptor(ChuckerInterceptor(application))
            .addInterceptor(loggerInterceptor)
        return httpClient.build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): BudgetService {
        return retrofit.create(BudgetService::class.java)
    }

    @Provides
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        factory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_URL)
            .client(okHttpClient)
            .addConverterFactory(factory)
            .build()
    }
    @Provides
    fun provideConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }
    @Provides
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }


    @Provides
    @Singleton
    fun imageLoader(
        okHttpCallFactory: dagger.Lazy<Call.Factory>,
        @ApplicationContext application: Context,
    ): ImageLoader = trace("BudgetImageLoader") {
        ImageLoader.Builder(application)
            .callFactory { okHttpCallFactory.get() }
            .components { add(SvgDecoder.Factory()) }
            .respectCacheHeaders(false)
//            .apply {
//                if (BuildConfig.DEBUG) {
//                    logger(DebugLogger())
//                }
//            }
            .build()
    }


}