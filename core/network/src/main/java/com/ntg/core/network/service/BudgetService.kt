package com.ntg.core.network.service

import com.ntg.core.network.model.ResponseBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface BudgetService {

    @FormUrlEncoded
    @POST(value = "/api/request_otp")
    suspend fun sendVerificationCode(
        @Field("query") phone: String,
    ): Response<ResponseBody<String?>>



}