package com.example.wakati_kotlin.api

import android.util.Log
import com.example.wakati_kotlin.model.AssignDealerRequest
import com.example.wakati_kotlin.model.DashboardResponse
import com.example.wakati_kotlin.model.DealersResponse
import com.example.wakati_kotlin.model.DepositRequest
import com.example.wakati_kotlin.model.IslandsResponse
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.model.StatementRequest
import com.example.wakati_kotlin.model.TransactionResponse
import com.example.wakati_kotlin.model.UserListResponse
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface API {

    @Headers(
        "Content-type: application/json", "Accept: application/json"
    )
    @POST("login")
    fun loginAuth(@Body jsonObject: JsonObject): Call<LoginResponse>


    @POST("update_customer_registration")
    fun updateAccount(@Body jsonObject: JsonObject): Call<LoginResponse>


    @POST("send_otp")
    fun verifyOtp(@Body jsonObject: JsonObject): Call<LoginResponse>


    @POST("withdraw_otp")
    fun withdrawOtp(@Body jsonObject: JsonObject): Call<LoginResponse>

    @GET("islands_regions")
    fun getIslands(@Header("x-language") language: String): Call<IslandsResponse>

    @Headers(
        "Content-type: application/json", "Accept: application/json"
    )
    @POST("mpin")
    fun loginPin(@Body jsonObject: JsonObject): Call<LoginResponse>

    @GET("dashboard")
    fun dashboard(
        @Header("Authorization") authToken: String, @Query("user_id") userId: String
    ): Call<LoginResponse>

    @POST("receiver_partner_agent_dashboard")
    fun receiverPartnerAgentDashboard(
        @Header("Authorization") token: String, @Body body: JsonObject
    ): Call<DashboardResponse>

    @GET("get_user_by_mobile")
    fun profileBymobile(
        @Header("Authorization") authToken: String, @Query("mobile_no") mobileNumber: String
    ): Call<LoginResponse>

    @POST("forgot_password_verification")
    fun forgotPasswordVerification(@Body jsonObject: JsonObject): Call<LoginResponse>

    @POST("forgot_password")
    fun forgotPassword(@Body jsonObject: JsonObject): Call<LoginResponse>

    @POST("mpin_reset")
    fun mpinReset(@Body jsonObject: JsonObject): Call<LoginResponse>

    @POST("otp_verification")
    fun otpVerification(@Body jsonObject: JsonObject): Call<LoginResponse>

    @POST("transaction_list")
    fun getTransactions(
        @Header("Authorization") token: String, @Body jsonObject: JsonObject
    ): Call<TransactionResponse>

    @Headers(
        "Content-type: application/json", "Accept: application/json"
    )
    @POST("transactions")
    fun transactionMoney(
        @Header("Authorization") token: String, @Body request: DepositRequest
    ): Call<LoginResponse>

    @Headers(
        "Content-Type: application/json", "Accept: application/json"
    )
    @POST("account_statement_download")
    fun downloadStatement(
        @Header("Authorization") token: String, @Body request: StatementRequest
    ): Call<ResponseBody>


    @POST("super_dealers_list")
    fun getSuperDealers(
        @Header("Authorization") authToken: String, @Body body: JsonObject
    ): Call<UserListResponse>


    @POST("dealer_list")
    fun getDealers(
        @Header("Authorization") authToken: String, @Body body: JsonObject
    ): Call<UserListResponse>


    @POST("partner_agent_list")
    fun getPartnerAgents(
        @Header("Authorization") authToken: String, @Body body: JsonObject
    ): Call<UserListResponse>

    @Headers(
        "Content-Type: application/json", "Accept: application/json"
    )


    @POST("receiver_front_desk_list")
    fun getFrontDesk(@Header("Authorization") token: String, @Query("user_id") userId: String
    ): Call<UserListResponse>


    @POST("assign_dealers")
    fun assignDealers(@Header("Authorization") authToken: String, @Body body: AssignDealerRequest

    ): Call<LoginResponse>

}