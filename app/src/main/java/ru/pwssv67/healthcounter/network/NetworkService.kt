package ru.pwssv67.healthcounter.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkService {
    private const val WEATHER_URL = "http://api.weatherapi.com"
    public const val key = "b43618f4ff7848e4910122737210302"
    private const val POSTS_URL = "https://jsonplaceholder.typicode.com"

    private var retrofit:Retrofit
    val weatherApi:WeatherApi
    val postsApi:PostsApi
    init {
        retrofit = Retrofit.Builder()
            .baseUrl(WEATHER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        weatherApi = retrofit.create(WeatherApi::class.java)

        retrofit = Retrofit.Builder()
            .baseUrl(POSTS_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        postsApi = retrofit.create(PostsApi::class.java)

    }

}