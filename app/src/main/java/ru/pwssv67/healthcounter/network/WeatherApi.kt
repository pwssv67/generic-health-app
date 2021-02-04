package ru.pwssv67.healthcounter.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.pwssv67.healthcounter.models.WeatherForecastModel

interface WeatherApi {
    @GET("/v1/current.json")
    fun getData(@Query("key") key:String, @Query("q") q:String): Call<WeatherForecastModel>
}