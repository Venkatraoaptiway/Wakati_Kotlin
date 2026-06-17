package com.example.wakati_kotlin.model

import com.google.gson.annotations.SerializedName

data class BlockUserRequest(

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("status")
    val status: String
)