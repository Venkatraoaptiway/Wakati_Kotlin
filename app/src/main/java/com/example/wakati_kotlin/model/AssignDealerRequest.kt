package com.example.wakati_kotlin.model;

import com.google.gson.annotations.SerializedName

data class AssignDealerRequest(

    @SerializedName("super_dealer_id")
    val superDealerId: String,

    @SerializedName("receiver_id")
    val receiverId: String,

    @SerializedName("dealer_ids")
    val dealerIds: ArrayList<String>,

    @SerializedName("action")
    val action: String? = null
)
