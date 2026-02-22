package com.borhan.currencygate

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {
    @GET("latest")
    suspend fun latest(
        @Query("amount") amount: Double,
        @Query("from") from: String,
        @Query("to") to: String
    ): CurrencyResponse
}

data class CurrencyResponse(
    val amount: Double,
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)

object CurrencyApiFactory {
    val api: CurrencyApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.frankfurter.app/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(CurrencyApi::class.java)
    }
}
