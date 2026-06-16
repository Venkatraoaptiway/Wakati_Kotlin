package com.example.wakati_kotlin.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @SerializedName("code")
    val code: Int = 0,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("trace_id")
    val traceId: String? = null,

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("expires_in")
    val expiresIn: Int = 0,

    @SerializedName("refresh_token")
    val refreshToken: String? = null,

    @SerializedName("refresh_expires_in")
    val refreshExpiresIn: Int = 0,

    @SerializedName("data")
    val data: UserData? = null,

    @SerializedName("profileData")
    val profileData: ProfileData? = null,

    @SerializedName("walletData")
    val walletData: WalletData? = null,

    @SerializedName("response")
    val response: Response? = null,

    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("otp_id")
    val otpId: String? = null
) {
    data class Response(

        @SerializedName("user_id")
        val userId: String? = null,

        @SerializedName("fullName")
        val fullName: String? = null
    )
}