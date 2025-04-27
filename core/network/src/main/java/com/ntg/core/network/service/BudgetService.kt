package com.ntg.core.network.service

import com.ntg.core.model.res.Bank
import com.ntg.core.model.res.CategoryRes
import com.ntg.core.model.res.CodeVerification
import com.ntg.core.model.res.Currency
import com.ntg.core.model.res.ServerAccount
import com.ntg.core.model.res.ServerConfig
import com.ntg.core.model.res.ServerContacts
import com.ntg.core.model.res.SessionsResItem
import com.ntg.core.model.res.SyncContactsRes
import com.ntg.core.model.res.SyncedAccount
import com.ntg.core.model.res.SyncedWallet
import com.ntg.core.model.res.UploadAvatarRes
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
    @POST(value = "/api/v1/request_otp")
    suspend fun sendVerificationCode(
        @Field("query") phone: String,
    ): Response<ResponseBody<String?>>


    @FormUrlEncoded
    @POST(value = "/api/v1/verify_otp")
    suspend fun verifyOtp(
        @Field("otp") otp: Int,
        @Field("query") query: String,
    ): Response<CodeVerification?>


    @GET(value = "/api/v1/account/my")
    suspend fun my(): Response<List<ServerAccount>>


    @FormUrlEncoded
    @POST(value = "/api/v1/account/create")
    suspend fun syncAccount(
        @Field("name") name: String,
    ): Response<SyncedAccount?>

    @FormUrlEncoded
    @POST(value = "/api/v1/account/update")
    suspend fun updateAccount(
        @Field("name") name: String,
        @Field("id") id: String,
    ): Response<SyncedAccount?>

    @FormUrlEncoded
    @POST(value = "/api/v1/account/destroy")
    suspend fun deleteAccount(
        @Field("id") id: String,
    ): Response<ResponseBody<String?>>

    @GET(value = "/api/v1/wallet/types")
    suspend fun walletTypes(
    ): Response<List<WalletType>?>

    @GET(value = "/api/v1/banks")
    suspend fun banks(): Response<List<Bank>?>

    @GET(value = "/api/v1/config")
    suspend fun configs(
        @Query("platform") platform: String = "all"
    ): Response<List<ServerConfig>>

    @GET(value = "/api/v1/currencies")
    suspend fun currencies(): Response<List<Currency>?>

    @FormUrlEncoded
    @POST(value = "/api/v1/wallet/create")
    suspend fun syncWallet(
        @Field("wallet_type_id") walletType: String,
        @Field("currency_id") currencyId: String,
        @Field("account_id") accountId: String,
        @Field("details") details: String,
//        @Field("init_amount") initAmount: String,
    ): Response<SyncedAccount?>

    @FormUrlEncoded
    @POST(value = "/api/v1/person/create")
    suspend fun syncContacts(
        @Field("full_name") fullName: String? = null,
        @Field("mobile_number") mobileNumber: String? = null,
        @Field("bank_accounts") bankAccount: String? = null,
        @Field("phone_number") phoneNumber: String? = null,
        @Field("email") email: String? = null,
        @Field("address") address: String? = null,
        @Field("note") note: String? = null,
    ): Response<SyncContactsRes?>

    @FormUrlEncoded
    @POST(value = "/api/v1/wallet/destroy")
    suspend fun deleteWallet(
        @Field("wallet_id") id: String,
    ): Response<ResponseBody<String?>>

    @FormUrlEncoded
    @POST(value = "/api/v1/wallet/update")
    suspend fun updateWallet(
        @Field("wallet_id") id: String,
        @Field("details") details: String,
    ): Response<SyncedWallet?>

    @GET(value = "/api/v1/category/get")
    suspend fun categories(): Response<CategoryRes?>

    @GET(value = "/api/v1/user")
    suspend fun getUser(): Response<UserInfo>

    @FormUrlEncoded
    @POST("/api/v1/user/upload_avatar")
    suspend fun uploadAvatar(
        @Field("image_base64") image: String
    ): Response<UploadAvatarRes>

    @FormUrlEncoded
    @POST("/api/v1/user/update_user_data")
    suspend fun uploadUserData(
        @Field("full_name") name: String,
        @Field("username") username: String
    ): Response<ResponseBody<String?>>

    @GET("/api/v1/session/list")
    suspend fun sessionsList(): Response<List<SessionsResItem>>

    @GET("/api/v1/session/terminate_all")
    suspend fun terminateAllSessions(): Response<ResponseBody<String?>>

    @FormUrlEncoded
    @POST("/api/v1/session/terminate")
    suspend fun terminateSession(
        @Field("session_id") sessionId: String,
    ): Response<ResponseBody<String?>>

    @GET("/api/v1/person/list?page=1&per_page=100")
    suspend fun serverContacts(): Response<ServerContacts?>
}