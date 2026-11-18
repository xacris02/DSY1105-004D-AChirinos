package com.example.insercodechirinosestobar.model

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val nombre: String = "",
    val isLoading: Boolean = false,
    val mensajeError: String? = null,
    val recordarme: Boolean = true,
    val mensajeInfo: String? = null,
    val rol: Rol? = null
) {
    // El botón siempre está habilitado, salvo mientras carga
    val isLoginEnabled: Boolean
        get() = !isLoading
}
