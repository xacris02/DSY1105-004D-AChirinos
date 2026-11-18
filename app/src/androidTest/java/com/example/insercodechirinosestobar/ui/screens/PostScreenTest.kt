package com.example.insercodechirinosestobar.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import com.example.insercodechirinosestobar.data.model.Post
import com.example.insercodechirinosestobar.data.remote.ApiService
import com.example.insercodechirinosestobar.repository.PostRepository
import com.example.insercodechirinosestobar.viewmodel.PostViewModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import retrofit2.Response

class PostScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val fakePosts = listOf(
        Post(userId = 1, id = 1, title = "Título 1", body = "Contenido 1"),
        Post(userId = 2, id = 2, title = "Título 2", body = "Contenido 2")
    )

    // API falsa: no usa red, devuelve los datos simulados
    private val fakeApi = object : ApiService {
        override suspend fun getPosts() = fakePosts
        override suspend fun createPost(post: Post) = post.copy(id = 101)
        override suspend fun updatePost(id: Int, post: Post) = post
        override suspend fun deletePost(id: Int) = Response.success(Unit)
    }

    @Test
    fun el_titulo_de_post_debe_aparecer_en_pantalla() {
        val viewModel = PostViewModel(PostRepository(fakeApi))

        composeRule.setContent { PostScreen(viewModel = viewModel) }

        // El ViewModel carga los datos de forma asíncrona: esperamos a que aparezcan
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Título: Título 1").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Título: Título 1").assertIsDisplayed()
        composeRule.onNodeWithText("Título: Título 2").assertIsDisplayed()
    }

    @Test
    fun muestra_el_cuerpo_de_cada_post() {
        composeRule.setContent {
            PostContenido(fakePosts, false, null, {}, {}, { _, _ -> }, {})
        }
        composeRule.onNodeWithText("Contenido 1").assertIsDisplayed()
        composeRule.onNodeWithText("Contenido 2").assertIsDisplayed()
    }

    @Test
    fun muestra_error_y_boton_reintentar_si_no_hay_datos() {
        var reintentos = 0
        composeRule.setContent {
            PostContenido(
                posts = emptyList(),
                cargando = false,
                error = "No se pudieron cargar los posts",
                onVolver = {},
                onReintentar = { reintentos++ },
                onCrear = { _, _ -> },
                onEliminar = {}
            )
        }
        composeRule.onNodeWithText("No se pudieron cargar los posts").assertIsDisplayed()
        composeRule.onNodeWithText("Reintentar").performClick()
        assertEquals(1, reintentos)
    }

    @Test
    fun boton_eliminar_notifica_el_id_del_post() {
        var eliminado = -1
        composeRule.setContent {
            PostContenido(listOf(fakePosts[0]), false, null, {}, {}, { _, _ -> }, { eliminado = it })
        }
        composeRule.onNodeWithContentDescription("Eliminar post").performClick()
        assertEquals(1, eliminado)
    }
}
