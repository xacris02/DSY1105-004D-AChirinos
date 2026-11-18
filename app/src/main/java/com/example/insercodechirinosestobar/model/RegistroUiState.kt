package com.example.insercodechirinosestobar.model

data class RegistroUiState(
    val nombre: String = "",
    val password: String = "",
    val confirmarPassword: String = "",
    val isLoading: Boolean = false,
    val mensajeError: String? = null
)

data class RegistroErrores(
    val nombre: String? = null,
    val password: String? = null,
    val confirmarPassword: String? = null
)
