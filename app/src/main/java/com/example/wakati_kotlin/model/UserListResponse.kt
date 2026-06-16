package com.example.wakati_kotlin.model

import com.google.gson.annotations.SerializedName

data class UserListResponse(

    val code: Int?,
    val message: String?,

    @SerializedName("content")
    val content: ArrayList<DealerModel>?,

    @SerializedName("data")
    val data: ArrayList<DealerModel>?

)