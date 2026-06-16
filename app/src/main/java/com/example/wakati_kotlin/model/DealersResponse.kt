package com.example.wakati_kotlin.model

data class DealersResponse(

    val code: Int,
    val message: String,
    val count: Int,
    val content: ArrayList<DealerModel>

)