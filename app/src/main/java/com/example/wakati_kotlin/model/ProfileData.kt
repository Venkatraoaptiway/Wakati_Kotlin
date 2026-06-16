package com.example.wakati_kotlin.model

import com.google.gson.annotations.SerializedName

data class ProfileData(

    @SerializedName("user_id")
    val userId: String? = null,

    @SerializedName("userType")
    val userType: String? = null,

    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("mobileNo")
    val mobileNo: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("Island")
    val island: String? = null,

    @SerializedName("Region")
    val region: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("createdBy")
    val createdBy: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("registration_stage")
    val registrationStage: String? = null,

    @SerializedName("verified_by_admin")
    val verifiedByAdmin: String? = null,

    @SerializedName("verified_by_adjudicator")
    val verifiedByAdjudicator: String? = null,

    @SerializedName("attributes")
    val attributes: List<Attribute> = emptyList(),

    @SerializedName("documents")
    val documents: List<Document> = emptyList()
)