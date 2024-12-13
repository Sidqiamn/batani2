package com.example.batani.network

import com.google.gson.annotations.SerializedName

data class CodeResponse(
    @SerializedName("code") val cityCode: String?
)


data class CityRequest(
    @SerializedName("name") val name: String
)

