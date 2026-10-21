package com.example.insercodechirinosestobar.viewmodel

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.insercodechirinosestobar.model.LoginErrores
import com.example.insercodechirinosestobar.model.LoginUiState
import com.example.insercodechirinosestobar.repository.ConexionException
import com.example.insercodechirinosestobar.repository.CredencialesException
import com.example.insercodechirinosestobar.repository.SesionRepository
import com.example.insercodechirinosestobar.repository.UsuarioRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val usuarioRepository = UsuarioRepository(app.applicationContext)
    private val sesionRepository = SesionRepository(app.applicationContext)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _errores = MutableStateFlow(LoginErrores())
    val errores: StateFlow<LoginErrores> = _errores.asStateFlow()

    init {
        // Carga el último correo guardado
        viewModelScope.launch {
            try {
                val email = sesionRepository.emailGuardado.first()
                _uiState.update { if (it.email.isBlank()) it.copy(email = email) else it }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Si falla, la app sigue funcionando
            }
        }
    }

    fun onEmailChange(valor: String) {
        _uiState.update { it.copy(email = valor, mensajeError = null) }
        _errores.update { it.copy(email = null) }
    }

    fun onPasswordChange(valor: String) {
        _uiState.update { it.copy(password = valor, mensajeError = null) }
        _errores.update { it.copy(password = null) }
    }

    fun validarFormulario(): Boolean {
        val estado = _uiState.value
        val errores = LoginErrores(
            email = when {
                estado.email.isBlank() -> "Debes ingresar un correo"
                !Patterns.EMAIL_ADDRESS.matcher(estado.email.trim()).matches() ->
                    "Ingresa un correo válido"
                else -> null
            },
            password = when {
                estado.password.isBlank() -> "Debes ingresar una contraseña"
                estado.password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
                else -> null
            }
        )
        _errores.value = errores
        return errores.email == null && errores.password == null
    }

    fun iniciarSesion() {
        if (_uiState.value.isLoading || !validarFormulario()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mensajeError = null) }
            val estado = _uiState.value
            try {
                val usuario = usuarioRepository.autenticar(estado.email, estado.password)
                runCatching { sesionRepository.guardarEmail(estado.email.trim()) }
                _uiState.update { it.copy(isLoading = false, password = "", rol = usuario.rol) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: CredencialesException) {
                mostrarError("Correo o contraseña incorrectos")
            } catch (e: ConexionException) {
                mostrarError("Sin conexión. Revisa tu red e inténtalo de nuevo")
            } catch (e: Exception) {
                // Cualquier otro error no debe cerrar la app
                mostrarError("Ocurrió un error inesperado")
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        _uiState.update { it.copy(isLoading = false, mensajeError = mensaje) }
    }

    // Deja el login en blanco
    fun cerrarSesion() {
        _uiState.value = LoginUiState()
        _errores.value = LoginErrores()
        viewModelScope.launch { runCatching { sesionRepository.borrarEmail() } }
    }
}
