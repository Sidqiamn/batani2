package com.example.batani.network

import com.google.gson.JsonElement
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import java.lang.reflect.Type

class RekomendasiResponseInstanceCreator : JsonDeserializer<RekomendasiResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): RekomendasiResponse {
        val jsonObject = json?.asJsonObject
        val rekomendasi1 = jsonObject?.get("Rekomendasi ke-1")?.asString ?: ""
        val rekomendasi2 = jsonObject?.get("Rekomendasi ke-2")?.asString ?: ""
        val rekomendasi3 = jsonObject?.get("Rekomendasi ke-3")?.asString ?: ""

        return RekomendasiResponse(rekomendasi1, rekomendasi2, rekomendasi3)
    }
}
