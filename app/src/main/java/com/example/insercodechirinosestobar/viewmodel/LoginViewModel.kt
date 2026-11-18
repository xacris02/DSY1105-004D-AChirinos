package com.example.insercodechirinosestobar.viewmodel

import android.app.Application
import android.content.Context
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.insercodechirinosestobar.model.LoginErrores
import com.example.insercodechirinosestobar.model.LoginUiState
import com.example.insercodechirinosestobar.model.RegistroErrores
import com.example.insercodechirinosestobar.model.RegistroUiState
import com.example.insercodechirinosestobar.model.Usuario
import com.example.insercodechirinosestobar.repository.ConexionException
import com.example.insercodechirinosestobar.repository.CredencialesException
import com.example.insercodechirinosestobar.repository.GoogleException
import com.example.insercodechirinosestobar.repository.OperacionRepository
import com.example.insercodechirinosestobar.repository.SesionRepository
import com.example.insercodechirinosestobar.repository.UsuarioExistenteException
import com.example.insercodechirinosestobar.repository.UsuarioRepository
import com.example.insercodechirinosestobar.repository.normalizarNombre
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
    private val operacionRepository = OperacionRepository(app.applicationContext)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _errores = MutableStateFlow(LoginErrores())
    val errores: StateFlow<LoginErrores> = _errores.asStateFlow()

    private val _registro = MutableStateFlow(RegistroUiState())
    val registro: StateFlow<RegistroUiState> = _registro.asStateFlow()

    private val _erroresRegistro = MutableStateFlow(RegistroErrores())
    val erroresRegistro: StateFlow<RegistroErrores> = _erroresRegistro.asStateFlow()

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

    // ---------- Login ----------

    fun onRecordarmeChange(valor: Boolean) {
        _uiState.update { it.copy(recordarme = valor) }
    }

    // "¿Olvidaste tu contraseña?": envía el correo de restablecimiento (solo correos reales)
    fun restablecerPassword() {
        val correo = _uiState.value.email.trim()
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _uiState.update { it.copy(mensajeInfo = null, mensajeError = "Escribe tu correo para restablecer la contraseña") }
            return
        }
        viewModelScope.launch {
            try {
                usuarioRepository.restablecerPassword(correo)
                _uiState.update { it.copy(mensajeError = null, mensajeInfo = "Te enviamos un correo para restablecer tu contraseña") }
            } catch (e: CancellationException) {
                throw e
            } catch (e: ConexionException) {
                mostrarError("Sin conexión. Revisa tu red e inténtalo de nuevo")
            } catch (e: Exception) {
                mostrarError("No se pudo enviar el correo de restablecimiento")
            }
        }
    }

    fun onEmailChange(valor: String) {
        _uiState.update { it.copy(email = valor, mensajeError = null, mensajeInfo = null) }
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
                estado.email.isBlank() -> "Debes ingresar tu nombre o correo"
                estado.email.contains("@") &&
                    !Patterns.EMAIL_ADDRESS.matcher(estado.email.trim()).matches() ->
                    "Ingresa un correo válido"
                !estado.email.contains("@") && normalizarNombre(estado.email).isEmpty() ->
                    "Ingresa un nombre válido"
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
                val usuario = usuarioRepository.iniciarSesion(estado.email, estado.password)
                completarSesion(usuario, estado.email.trim())
            } catch (e: CancellationException) {
                throw e
            } catch (e: CredencialesException) {
                mostrarError("Nombre/correo o contraseña incorrectos")
            } catch (e: ConexionException) {
                mostrarError("Sin conexión. Revisa tu red e inténtalo de nuevo")
            } catch (e: Exception) {
                // Cualquier otro error no debe cerrar la app
                mostrarError("Ocurrió un error inesperado")
            }
        }
    }

    // Botón "Continuar con Google": crea la cuenta si es nueva o inicia sesión si ya existe
    fun iniciarConGoogle(activityContext: Context) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, mensajeError = null) }
            try {
                val usuario = usuarioRepository.autenticarConGoogle(activityContext)
                completarSesion(usuario, usuario.email)
            } catch (e: CancellationException) {
                throw e
            } catch (e: GoogleException) {
                mostrarError(e.message ?: "No se pudo acceder con Google")
            } catch (e: ConexionException) {
                mostrarError("Sin conexión. Revisa tu red e inténtalo de nuevo")
            } catch (e: Exception) {
                mostrarError("No se pudo acceder con Google")
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        _uiState.update { it.copy(isLoading = false, mensajeError = mensaje) }
    }

    // ---------- Registro ----------

    fun onNombreRegistroChange(valor: String) {
        _registro.update { it.copy(nombre = valor, mensajeError = null) }
        _erroresRegistro.update { it.copy(nombre = null) }
    }

    fun onPasswordRegistroChange(valor: String) {
        _registro.update { it.copy(password = valor, mensajeError = null) }
        _erroresRegistro.update { it.copy(password = null, confirmarPassword = null) }
    }

    fun onConfirmarPasswordChange(valor: String) {
        _registro.update { it.copy(confirmarPassword = valor, mensajeError = null) }
        _erroresRegistro.update { it.copy(confirmarPassword = null) }
    }

    fun validarRegistro(): Boolean {
        val estado = _registro.value
        val errores = RegistroErrores(
            nombre = when {
                estado.nombre.isBlank() -> "Debes ingresar tu nombre"
                normalizarNombre(estado.nombre).isEmpty() -> "Ingresa un nombre válido"
                else -> null
            },
            password = when {
                estado.password.isBlank() -> "Debes ingresar una contraseña"
                estado.password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
                else -> null
            },
            confirmarPassword = when {
                estado.confirmarPassword.isBlank() -> "Debes confirmar la contraseña"
                estado.confirmarPassword != estado.password -> "Las contraseñas no coinciden"
                else -> null
            }
        )
        _erroresRegistro.value = errores
        return errores.nombre == null && errores.password == null && errores.confirmarPassword == null
    }

    fun registrar() {
        if (_registro.value.isLoading || !validarRegistro()) return

        viewModelScope.launch {
            _registro.update { it.copy(isLoading = true, mensajeError = null) }
            val estado = _registro.value
            try {
                val usuario = usuarioRepository.registrar(estado.nombre, estado.password)
                completarSesion(usuario, estado.nombre.trim())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mostrarErrorRegistro(e)
            }
        }
    }

    fun registrarConGoogle(activityContext: Context) {
        if (_registro.value.isLoading) return

        viewModelScope.launch {
            _registro.update { it.copy(isLoading = true, mensajeError = null) }
            try {
                val usuario = usuarioRepository.autenticarConGoogle(activityContext)
                completarSesion(usuario, usuario.email)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mostrarErrorRegistro(e)
            }
        }
    }

    private fun mostrarErrorRegistro(e: Exception) {
        val mensaje = when (e) {
            is UsuarioExistenteException -> "Ya existe una cuenta con ese nombre"
            is CredencialesException -> "No se pudo crear la cuenta. Revisa los datos"
            is ConexionException -> "Sin conexión. Revisa tu red e inténtalo de nuevo"
            is GoogleException -> e.message ?: "No se pudo acceder con Google"
            else -> "Ocurrió un error inesperado"
        }
        _registro.update { it.copy(isLoading = false, mensajeError = mensaje) }
    }

    fun limpiarRegistro() {
        _registro.value = RegistroUiState()
        _erroresRegistro.value = RegistroErrores()
    }

    // ---------- Sesión ----------

    // Deja el estado listo para entrar (el rol dispara la navegación a la pantalla de su perfil)
    private suspend fun completarSesion(usuario: Usuario, identificador: String) {
        val rol = runCatching { operacionRepository.rolDe(usuario.email, usuario.nombre) }
            .getOrDefault(rolPorDefecto(usuario.email))
        if (rol == null) {
            // El administrador desactivó este usuario de prueba
            runCatching { usuarioRepository.cerrarSesion() }
            val mensaje = "Tu usuario está desactivado. Contacta al administrador"
            _uiState.update { it.copy(isLoading = false, mensajeError = mensaje) }
            _registro.update { it.copy(isLoading = false, mensajeError = mensaje) }
            return
        }
        runCatching {
            if (_uiState.value.recordarme) sesionRepository.guardarEmail(identificador)
            else sesionRepository.borrarEmail()
        }
        _registro.value = RegistroUiState()
        _uiState.update {
            it.copy(
                isLoading = false,
                email = usuario.email,
                nombre = usuario.nombre,
                password = "",
                mensajeError = null,
                mensajeInfo = null,
                rol = rol
            )
        }
    }

    private fun rolPorDefecto(correo: String) = com.example.insercodechirinosestobar.model.rolSegunCorreo(correo)

    // Deja el login en blanco
    fun cerrarSesion() {
        runCatching { usuarioRepository.cerrarSesion() }
        _uiState.value = LoginUiState(email = _uiState.value.email.takeIf { _uiState.value.recordarme }.orEmpty(), recordarme = _uiState.value.recordarme)
        _errores.value = LoginErrores()
        limpiarRegistro()
        if (!_uiState.value.recordarme) viewModelScope.launch { runCatching { sesionRepository.borrarEmail() } }
    }
}
