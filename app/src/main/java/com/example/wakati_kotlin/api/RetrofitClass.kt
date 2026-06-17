package com.example.wakati_kotlin.api

import com.example.wakati_kotlin.model.AssignDealerRequest
import com.example.wakati_kotlin.model.BlockUserRequest
import com.example.wakati_kotlin.model.DashboardResponse
import com.example.wakati_kotlin.model.DepositRequest
import com.example.wakati_kotlin.model.IslandsResponse
import com.example.wakati_kotlin.model.LoginResponse
import com.example.wakati_kotlin.model.StatementRequest
import com.example.wakati_kotlin.model.TransactionResponse
import com.example.wakati_kotlin.model.UserListResponse
import com.example.wakati_kotlin.utils.Constants
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

class RetrofitClass(rootUrl: String) {

    private val retrofit: Retrofit


    init {
        val gson = GsonBuilder().setLenient().create()

        retrofit = Retrofit.Builder().baseUrl(rootUrl).addConverterFactory(
            GsonConverterFactory.create(gson)
        ).client(getUnsafeOkHttpClient()).build()
    }

    companion object {

        const val baseurl = Constants.BASE_URL


        const val PRE_ROOT_URL = baseurl

        private var retrofitClass: RetrofitClass? = null

        @JvmStatic
        fun getRetrofit(): RetrofitClass {
            if (retrofitClass == null) {
                retrofitClass = RetrofitClass(PRE_ROOT_URL)
            }
            return retrofitClass!!
        }


        private fun getUnsafeOkHttpClient(): OkHttpClient {

            return try {

                val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {

                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>, authType: String
                    ) {
                    }

                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>, authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                })

                val sslContext = SSLContext.getInstance("SSL")

                sslContext.init(
                    null, trustAllCerts, SecureRandom()
                )

                val sslSocketFactory = sslContext.socketFactory

                val builder = OkHttpClient.Builder()

                builder.sslSocketFactory(
                    sslSocketFactory, trustAllCerts[0] as X509TrustManager
                )

                builder.hostnameVerifier { _, _ -> true }

                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY

                builder.addInterceptor(logging)

                builder.readTimeout(
                    2, TimeUnit.MINUTES
                ).connectTimeout(
                    2, TimeUnit.MINUTES
                ).build()

            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    fun loginAuth(cb: Callback<LoginResponse>, jsonObject: JsonObject) {
        val apiService = retrofit.create(API::class.java)
        apiService.loginAuth(jsonObject).enqueue(cb)
    }

    fun updateAccount(cb: Callback<LoginResponse>, jsonObject: JsonObject) {
        retrofit.create(API::class.java).updateAccount(jsonObject).enqueue(cb)
    }

    fun verifyOtp(cb: Callback<LoginResponse>, jsonObject: JsonObject) {
        retrofit.create(API::class.java).verifyOtp(jsonObject).enqueue(cb)
    }

    fun withdrawOtp(cb: Callback<LoginResponse>, jsonObject: JsonObject) {
        retrofit.create(API::class.java).withdrawOtp(jsonObject).enqueue(cb)
    }

    fun getIslands(cb: Callback<IslandsResponse>, language: String) {
        retrofit.create(API::class.java).getIslands(language).enqueue(cb)
    }

    fun loginPin(cb: Callback<LoginResponse>, jsonObject: JsonObject) {
        retrofit.create(API::class.java).loginPin(jsonObject).enqueue(cb)
    }

    fun dashboard(cb: Callback<LoginResponse>, authToken: String, userId: String) {
        retrofit.create(API::class.java).dashboard(authToken, userId).enqueue(cb)
    }

    fun receiverPartnerAgentDashboard(
        cb: Callback<DashboardResponse>, authToken: String, jsonObject: JsonObject
    ) {
        retrofit.create(API::class.java).receiverPartnerAgentDashboard(authToken, jsonObject)
            .enqueue(cb)
    }

    fun profileBymobile(cb: Callback<LoginResponse>, authToken: String, mobileNumber: String) {
        retrofit.create(API::class.java).profileBymobile(authToken, mobileNumber).enqueue(cb)
    }

    fun forgotPasswordVerification(cb: Callback<LoginResponse>, jsonObject: JsonObject) {
        retrofit.create(API::class.java).forgotPasswordVerification(jsonObject).enqueue(cb)
    }

    fun forgotPassword(cb: Callback<LoginResponse>, jsonObject: JsonObject) {
        retrofit.create(API::class.java).forgotPassword(jsonObject).enqueue(cb)
    }

    fun mpinReset(cb: Callback<LoginResponse>, jsonObject: JsonObject) {
        retrofit.create(API::class.java).mpinReset(jsonObject).enqueue(cb)
    }

    fun otpVerification(cb: Callback<LoginResponse>, jsonObject: JsonObject) {
        retrofit.create(API::class.java).otpVerification(jsonObject).enqueue(cb)
    }

    fun getTransactions(cb: Callback<TransactionResponse>, token: String, jsonObject: JsonObject) {
        retrofit.create(API::class.java).getTransactions(token, jsonObject).enqueue(cb)
    }

    fun transactionMoney(cb: Callback<LoginResponse>, token: String, request: DepositRequest) {
        retrofit.create(API::class.java).transactionMoney(token, request).enqueue(cb)
    }

    fun downloadStatement(cb: Callback<ResponseBody>, token: String, request: StatementRequest) {
        retrofit.create(API::class.java).downloadStatement(token, request).enqueue(cb)
    }


    fun getSuperDealers(
        cb: Callback<UserListResponse>, authToken: String, jsonObject: JsonObject
    ) {
        retrofit.create(API::class.java).getSuperDealers(authToken, jsonObject).enqueue(cb)
    }

    fun getDealers(
        cb: Callback<UserListResponse>, authToken: String, jsonObject: JsonObject
    ) {
        retrofit.create(API::class.java).getDealers(authToken, jsonObject).enqueue(cb)
    }

    fun getPartnerAgents(
        cb: Callback<UserListResponse>, authToken: String, jsonObject: JsonObject
    ) {
        retrofit.create(API::class.java).getPartnerAgents(authToken, jsonObject).enqueue(cb)
    }

    fun getFrontDesk(cb: Callback<UserListResponse>, authToken: String, userId: String) {
        retrofit.create(API::class.java).getFrontDesk(authToken, userId).enqueue(cb)
    }





    fun blockSuperDealer(cb: Callback<LoginResponse>, authToken: String, body: BlockUserRequest) {
        retrofit.create(API::class.java).blockSuperDealer(authToken, body).enqueue(cb)
    }


    fun assignDealer(cb: Callback<LoginResponse>, authToken: String, body: AssignDealerRequest) {
        retrofit.create(API::class.java).assignDealers(authToken, body).enqueue(cb)
    }




}