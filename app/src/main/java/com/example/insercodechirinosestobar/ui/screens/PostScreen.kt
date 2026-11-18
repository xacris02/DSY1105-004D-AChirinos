package com.example.insercodechirinosestobar.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.insercodechirinosestobar.data.model.Post
import com.example.insercodechirinosestobar.viewmodel.PostViewModel

// Pantalla con estado: observa el ViewModel y delega el dibujo a PostContenido
@Composable
fun PostScreen(
    viewModel: PostViewModel,
    onVolver: () -> Unit = {}
) {
    val posts by viewModel.postList.collectAsState()
    val cargando by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    PostContenido(
        posts = posts,
        cargando = cargando,
        error = error,
        onVolver = onVolver,
        onReintentar = viewModel::fetchPosts,
        onCrear = viewModel::createPost,
        onEliminar = viewModel::deletePost
    )
}

// Pantalla sin estado propio (más simple de probar y previsualizar)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostContenido(
    posts: List<Post>,
    cargando: Boolean,
    error: String?,
    onVolver: () -> Unit,
    onReintentar: () -> Unit,
    onCrear: (String, String) -> Unit,
    onEliminar: (Int) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listado de Posts") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Crear post")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                cargando && posts.isEmpty() ->
                    CircularProgressIndicator(Modifier.align(Alignment.Center))

                error != null && posts.isEmpty() ->
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(error, color = MaterialTheme.colorScheme.error)
                        Button(onClick = onReintentar) { Text("Reintentar") }
                    }

                else ->
                    Column(Modifier.fillMaxSize()) {
                        if (error != null) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
                        ) {
                            items(posts) { post ->
                                PostCard(post = post, onEliminar = { onEliminar(post.id) })
                            }
                        }
                    }
            }
        }
    }

    if (mostrarDialogo) {
        NuevoPostDialogo(
            onCancelar = { mostrarDialogo = false },
            onGuardar = { titulo, cuerpo ->
                onCrear(titulo, cuerpo)
                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun PostCard(post: Post, onEliminar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Título: ${post.title}",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = post.body,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar post")
            }
        }
    }
}

@Composable
private fun NuevoPostDialogo(
    onCancelar: () -> Unit,
    onGuardar: (String, String) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var cuerpo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Nuevo post") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") }
                )
                OutlinedTextField(
                    value = cuerpo,
                    onValueChange = { cuerpo = it },
                    label = { Text("Contenido") }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onGuardar(titulo.trim(), cuerpo.trim()) },
                enabled = titulo.isNotBlank() && cuerpo.isNotBlank()
            ) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
