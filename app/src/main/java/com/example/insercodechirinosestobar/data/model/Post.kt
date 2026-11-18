package com.example.insercodechirinosestobar.data.model

// Representa un post obtenido desde la API (jsonplaceholder.typicode.com)
data class Post(
    val userId: Int,   // ID del usuario que creó el post
    val id: Int,       // ID del post
    val title: String, // Título del post
    val body: String   // Cuerpo o contenido del post
)
