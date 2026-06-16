package com.example.wakati_kotlin.model

import com.google.gson.annotations.SerializedName

data class IslandsResponse(

    @SerializedName("code")
    val code: Int = 0,

    @SerializedName("content")
    val content: List<Island> = emptyList()
) {

    data class Island(

        @SerializedName("islandId")
        val islandId: String? = null,

        @SerializedName("name_en")
        val nameEn: String? = null,

        @SerializedName("regions")
        val regions: List<Region> = emptyList()
    )

    data class Region(

        @SerializedName("regionId")
        val regionId: String? = null,

        @SerializedName("region_en")
        val regionEn: String? = null,

        @SerializedName("cities")
        val cities: List<City> = emptyList()
    )

    data class City(

        @SerializedName("cityId")
        val cityId: String? = null,

        @SerializedName("city_en")
        val cityEn: String? = null
    )
}