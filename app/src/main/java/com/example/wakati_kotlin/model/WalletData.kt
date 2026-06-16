package com.example.wakati_kotlin.model

import com.google.gson.annotations.SerializedName

data class WalletData(

    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("userType")
    val userType: String? = null,

    @SerializedName("todayDeposit")
    val todayDeposit: Double = 0.0,

    @SerializedName("todayWithdraw")
    val todayWithdraw: Double = 0.0,

    @SerializedName("todayCommission")
    val todayCommission: Double = 0.0,

    @SerializedName("runningNotificationCount")
    val runningNotificationCount: Int = 0,

    @SerializedName("walletBalance")
    val walletBalance: Double = 0.0,

    @SerializedName("openingBalance")
    val openingBalance: Double = 0.0,

    @SerializedName("securityDeposit")
    val securityDeposit: Double = 0.0,

    @SerializedName("cashbalance")
    val cashbalance: Double = 0.0,

    @SerializedName("todaySourceCollection")
    val todaySourceCollection: Double = 0.0,

    @SerializedName("todaySourceDistributions")
    val todaySourceDistributions: Double = 0.0,

    @SerializedName("todayDealersCollection")
    val todayDealersCollection: Double = 0.0,

    @SerializedName("todayDealersDistributions")
    val todayDealersDistributions: Double = 0.0
)