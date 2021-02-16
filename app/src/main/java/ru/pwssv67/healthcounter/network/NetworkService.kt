package ru.pwssv67.healthcounter.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {
    private const val WEATHER_URL = "http://api.weatherapi.com"
    public const val key = "b43618f4ff7848e4910122737210302"
    val retrofit:Retrofit
    val weatherApi:WeatherApi
    init {
        retrofit = Retrofit.Builder()
            .baseUrl(WEATHER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApi = retrofit.create(WeatherApi::class.java)
    }

}