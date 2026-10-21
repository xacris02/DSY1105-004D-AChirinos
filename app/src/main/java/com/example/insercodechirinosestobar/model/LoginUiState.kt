package com.example.insercodechirinosestobar.model

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val mensajeError: String? = null,
    val rol: Rol? = null
) {
    // El botón siempre está habilitado, salvo mientras carga
    val isLoginEnabled: Boolean
        get() = !isLoading
}
