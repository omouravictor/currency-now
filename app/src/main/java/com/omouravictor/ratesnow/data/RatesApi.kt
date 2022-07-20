package com.omouravictor.ratesnow.data

import com.omouravictor.ratesnow.data.models.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesApi {

    @GET("/exchangerates_data/latest")
    suspend fun getRates(
        @Query("base") fromCurrency: String,
        @Query("symbols") toCurrencies: String,
        @Query("apikey") apikey: String
    ): Response<ApiResponse>
}