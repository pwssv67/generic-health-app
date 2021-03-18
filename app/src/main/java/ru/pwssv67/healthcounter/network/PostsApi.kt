package ru.pwssv67.healthcounter.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.pwssv67.healthcounter.models.Post

interface PostsApi {
    @GET("/posts")
    fun getData(): Call<MutableList<Post>>
}