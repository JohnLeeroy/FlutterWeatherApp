package com.example.weatherapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRequests {

    @GET("data/2.5/weather?appid=39cd05bb8fe12cfa3496ebfd35058023")
    fun getCurrentWeatherData(
        @Query("q") location: String
    ): Call<CurrentWeatherResponse>
}

