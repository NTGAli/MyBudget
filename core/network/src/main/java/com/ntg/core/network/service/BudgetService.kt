package com.ntg.core.network.service

import com.ntg.core.model.res.CodeVerification
import com.ntg.core.model.res.ServerAccount
import com.ntg.core.network.model.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

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



}