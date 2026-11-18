package com.example.insercodechirinosestobar.data.remote

import com.example.insercodechirinosestobar.data.model.Post
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Define los endpoints HTTP de la API
interface ApiService {

    @GET("posts")
    suspend fun getPosts(): List<Post>

    // Guía 14, punto 11: otros métodos HTTP
    @POST("posts")
    suspend fun createPost(@Body post: Post): Post

    @PUT("posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: Post): Post

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Response<Unit>
}
