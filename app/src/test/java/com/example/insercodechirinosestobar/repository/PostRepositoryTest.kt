package com.example.insercodechirinosestobar.repository

import com.example.insercodechirinosestobar.data.model.Post
import com.example.insercodechirinosestobar.data.remote.ApiService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import retrofit2.Response
import java.io.IOException

class PostRepositoryTest : StringSpec({

    val fakePosts = listOf(
        Post(userId = 1, id = 1, title = "Título 1", body = "Cuerpo 1"),
        Post(userId = 2, id = 2, title = "Título 2", body = "Cuerpo 2")
    )

    "getPosts() debe retornar una lista de posts simulada" {
        // El ApiService falso se inyecta por constructor (no hace falta una subclase)
        val mockApi = mockk<ApiService>()
        coEvery { mockApi.getPosts() } returns fakePosts
        val repo = PostRepository(mockApi)

        runTest {
            repo.getPosts() shouldContainExactly fakePosts
        }
    }

    "getPosts() propaga el error cuando la API falla" {
        val mockApi = mockk<ApiService>()
        coEvery { mockApi.getPosts() } throws IOException("sin red")
        val repo = PostRepository(mockApi)

        runTest {
            shouldThrow<IOException> { repo.getPosts() }
        }
    }

    "createPost() retorna el post creado por la API" {
        val mockApi = mockk<ApiService>()
        val nuevo = Post(1, 0, "Nuevo", "Cuerpo")
        coEvery { mockApi.createPost(nuevo) } returns nuevo.copy(id = 101)
        val repo = PostRepository(mockApi)

        runTest {
            repo.createPost(nuevo).id shouldBe 101
        }
    }

    "deletePost() llama al endpoint DELETE con el id correcto" {
        val mockApi = mockk<ApiService>()
        coEvery { mockApi.deletePost(5) } returns Response.success(Unit)
        val repo = PostRepository(mockApi)

        runTest {
            repo.deletePost(5)
            coVerify(exactly = 1) { mockApi.deletePost(5) }
        }
    }
})
