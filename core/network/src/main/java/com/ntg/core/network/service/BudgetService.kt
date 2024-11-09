package com.ntg.core.network.service

import com.ntg.core.model.res.Bank
import com.ntg.core.model.res.CategoryRes
import com.ntg.core.model.res.CodeVerification
import com.ntg.core.model.res.Currency
import com.ntg.core.model.res.ServerAccount
import com.ntg.core.model.res.ServerConfig
import com.ntg.core.model.res.SyncedAccount
import com.ntg.core.model.res.SyncedWallet
import com.ntg.core.model.res.UserInfo
import com.ntg.core.model.res.WalletType
import com.ntg.core.network.model.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BudgetService {

    @FormUrlEncoded
    @POST(value = "/api/request_otp")
    suspend fun sendVerificationCode(
        @Field("query") phone: String,
    ): Response<ResponseBody<String?>>


    @FormUrlEncoded
    @POST(value = "/api/verify_otp")
    suspend fun verifyOtp(
        @Field("otp") otp: Int,
        @Field("query") query: String,
    ): Response<CodeVerification?>


    @GET(value = "/api/account/my")
    suspend fun my(): Response<List<ServerAccount>>


    @FormUrlEncoded
    @POST(value = "/api/account/create")
    suspend fun syncAccount(
        @Field("name") name: String,
    ): Response<SyncedAccount?>

    @FormUrlEncoded
    @POST(value = "/api/account/update")
    suspend fun updateAccount(
        @Field("name") name: String,
        @Field("id") id: String,
    ): Response<SyncedAccount?>

    @FormUrlEncoded
    @POST(value = "/api/account/destroy")
    suspend fun deleteAccount(
        @Field("id") id: String,
    ): Response<ResponseBody<String?>>

    @GET(value = "/api/wallet/types")
    suspend fun walletTypes(
    ): Response<List<WalletType>?>

    @GET(value = "/api/banks")
    suspend fun banks(): Response<List<Bank>?>

    @GET(value = "/api/config")
    suspend fun configs(
        @Query("platform") platform: String = "all"
    ): Response<List<ServerConfig>>

    @GET(value = "/api/currencies")
    suspend fun currencies(): Response<List<Currency>?>

    @FormUrlEncoded
    @POST(value = "/api/wallet/create")
    suspend fun syncWallet(
        @Field("wallet_type_id") walletType: String,
        @Field("currency_id") currencyId: String,
        @Field("account_id") accountId: String,
        @Field("details") details: String,
//        @Field("init_amount") initAmount: String,
    ): Response<SyncedAccount?>

    @FormUrlEncoded
    @POST(value = "/api/wallet/destroy")
    suspend fun deleteWallet(
        @Field("wallet_id") id: String,
    ): Response<ResponseBody<String?>>

    @FormUrlEncoded
    @POST(value = "/api/wallet/update")
    suspend fun updateWallet(
        @Field("wallet_id") id: String,
        @Field("details") details: String,
    ): Response<SyncedWallet?>

    @GET(value = "/api/category/get")
    suspend fun categories(): Response<CategoryRes?>

    @GET(value = "/api/user")
    suspend fun getUser(): Response<UserInfo>
}