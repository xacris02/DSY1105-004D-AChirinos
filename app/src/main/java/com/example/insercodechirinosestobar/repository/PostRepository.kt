package com.example.insercodechirinosestobar.repository

import com.example.insercodechirinosestobar.data.model.Post
import com.example.insercodechirinosestobar.data.remote.ApiService
import com.example.insercodechirinosestobar.data.remote.RetrofitInstance
import retrofit2.HttpException

// Accede a los datos de posts usando Retrofit.
// El ApiService se recibe por constructor para poder reemplazarlo en los tests.
class PostRepository(private val api: ApiService = RetrofitInstance.api) {

    suspend fun getPosts(): List<Post> = api.getPosts()

    suspend fun createPost(post: Post): Post = api.createPost(post)

    suspend fun updatePost(id: Int, post: Post): Post = api.updatePost(id, post)

    suspend fun deletePost(id: Int) {
        val respuesta = api.deletePost(id)
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
    }
}
