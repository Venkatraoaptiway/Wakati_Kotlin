package com.example.wakati_kotlin.model;

import com.google.gson.annotations.SerializedName

data class Attribute(

    @SerializedName("attributeLabel")
    val attributeLabel: String? = null,

    @SerializedName("attributeValue")
    val attributeValue: String? = null
)

