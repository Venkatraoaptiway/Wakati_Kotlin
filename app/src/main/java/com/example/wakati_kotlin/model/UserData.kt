package com.example.wakati_kotlin.model

import com.google.gson.annotations.SerializedName

data class UserData(

    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("mobileNo")
    val mobileNo: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("userType")
    val userType: String? = null,

    @SerializedName("createdBy")
    val createdBy: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("attributes")
    val attributes: List<Attribute> = emptyList(),

    @SerializedName("documents")
    val documents: List<Document> = emptyList()
)