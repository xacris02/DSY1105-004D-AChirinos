package com.example.insercodechirinosestobar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.insercodechirinosestobar.data.model.Post
import com.example.insercodechirinosestobar.repository.PostRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Mantiene el estado de los posts obtenidos desde la API
class PostViewModel(private val repository: PostRepository) : ViewModel() {

    // Constructor sin parámetros para que viewModel() pueda crearlo
    constructor() : this(PostRepository())

    private val _postList = MutableStateFlow<List<Post>>(emptyList())
    val postList: StateFlow<List<Post>> = _postList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Se llama automáticamente al iniciar
    init {
        fetchPosts()
    }

    // GET: obtiene los datos en segundo plano
    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _postList.value = repository.getPosts()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudieron cargar los posts"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // POST: jsonplaceholder no guarda datos, siempre responde id 101,
    // por eso se asigna un id local único para la lista.
    fun createPost(title: String, body: String) {
        viewModelScope.launch {
            _error.value = null
            try {
                val creado = repository.createPost(Post(userId = 1, id = 0, title = title, body = body))
                val idLocal = (_postList.value.maxOfOrNull { it.id } ?: 0) + 1
                _postList.update { listOf(creado.copy(id = idLocal)) + it }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo crear el post"
            }
        }
    }

    // PUT: actualiza un post existente
    fun updatePost(post: Post) {
        viewModelScope.launch {
            _error.value = null
            try {
                val actualizado = repository.updatePost(post.id, post)
                _postList.update { lista -> lista.map { if (it.id == post.id) actualizado else it } }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo actualizar el post"
            }
        }
    }

    // DELETE: elimina un post
    fun deletePost(id: Int) {
        viewModelScope.launch {
            _error.value = null
            try {
                repository.deletePost(id)
                _postList.update { lista -> lista.filterNot { it.id == id } }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = "No se pudo eliminar el post"
            }
        }
    }
}
