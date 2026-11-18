package com.example.insercodechirinosestobar.viewmodel

import com.example.insercodechirinosestobar.data.model.Post
import com.example.insercodechirinosestobar.repository.PostRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest : StringSpec({

    // viewModelScope usa Dispatchers.Main, que no existe en un test JVM
    beforeTest { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    afterTest { Dispatchers.resetMain() }

    val fakePosts = listOf(
        Post(userId = 1, id = 1, title = "Título 1", body = "Contenido 1"),
        Post(userId = 2, id = 2, title = "Título 2", body = "Contenido 2")
    )

    "postList debe contener los datos esperados después de fetchPosts()" {
        val repo = mockk<PostRepository>()
        coEvery { repo.getPosts() } returns fakePosts

        runTest {
            val viewModel = PostViewModel(repo) // init llama a fetchPosts()
            viewModel.postList.value shouldContainExactly fakePosts
            viewModel.isLoading.value shouldBe false
            viewModel.error.value shouldBe null
        }
    }

    "si la API falla, postList queda vacío y se informa el error" {
        val repo = mockk<PostRepository>()
        coEvery { repo.getPosts() } throws IOException("sin red")

        runTest {
            val viewModel = PostViewModel(repo)
            viewModel.postList.value.shouldBeEmpty()
            viewModel.error.value shouldBe "No se pudieron cargar los posts"
            viewModel.isLoading.value shouldBe false
        }
    }

    "fetchPosts() puede reintentar después de un error" {
        val repo = mockk<PostRepository>()
        coEvery { repo.getPosts() } throws IOException("sin red")

        runTest {
            val viewModel = PostViewModel(repo)
            viewModel.error.value shouldBe "No se pudieron cargar los posts"

            coEvery { repo.getPosts() } returns fakePosts
            viewModel.fetchPosts()

            viewModel.postList.value shouldContainExactly fakePosts
            viewModel.error.value shouldBe null
        }
    }

    "createPost() agrega el nuevo post al inicio de la lista" {
        val repo = mockk<PostRepository>()
        coEvery { repo.getPosts() } returns fakePosts
        // jsonplaceholder siempre responde con id 101
        coEvery { repo.createPost(any()) } returns Post(1, 101, "Nuevo", "Cuerpo nuevo")

        runTest {
            val viewModel = PostViewModel(repo)
            viewModel.createPost("Nuevo", "Cuerpo nuevo")

            val lista = viewModel.postList.value
            lista.size shouldBe 3
            lista.first().title shouldBe "Nuevo"
            lista.first().id shouldBe 3 // id local único
        }
    }

    "deletePost() elimina el post de la lista" {
        val repo = mockk<PostRepository>()
        coEvery { repo.getPosts() } returns fakePosts
        coEvery { repo.deletePost(1) } returns Unit

        runTest {
            val viewModel = PostViewModel(repo)
            viewModel.deletePost(1)

            viewModel.postList.value shouldContainExactly listOf(fakePosts[1])
            coVerify(exactly = 1) { repo.deletePost(1) }
        }
    }

    "si deletePost() falla, la lista no cambia y se informa el error" {
        val repo = mockk<PostRepository>()
        coEvery { repo.getPosts() } returns fakePosts
        coEvery { repo.deletePost(any()) } throws IOException("sin red")

        runTest {
            val viewModel = PostViewModel(repo)
            viewModel.deletePost(1)

            viewModel.postList.value shouldContainExactly fakePosts
            viewModel.error.value shouldBe "No se pudo eliminar el post"
        }
    }

    "updatePost() reemplaza el post modificado" {
        val repo = mockk<PostRepository>()
        val editado = fakePosts[0].copy(title = "Editado")
        coEvery { repo.getPosts() } returns fakePosts
        coEvery { repo.updatePost(1, editado) } returns editado

        runTest {
            val viewModel = PostViewModel(repo)
            viewModel.updatePost(editado)

            viewModel.postList.value shouldContainExactly listOf(editado, fakePosts[1])
        }
    }
})
