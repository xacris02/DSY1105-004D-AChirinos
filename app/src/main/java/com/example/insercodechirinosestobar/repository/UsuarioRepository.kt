package com.example.insercodechirinosestobar.repository

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CredentialOption
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.insercodechirinosestobar.R
import com.example.insercodechirinosestobar.model.Rol
import com.example.insercodechirinosestobar.model.Usuario
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await
import java.text.Normalizer

class CredencialesException : Exception()
class ConexionException : Exception()
class UsuarioExistenteException : Exception()
class GoogleException(mensaje: String) : Exception(mensaje)

private const val DOMINIO_INTERNO = "modoguardian.app"

// "Juan Pérez" -> "juan.perez". Vacío si el nombre no tiene letras ni números
fun normalizarNombre(nombre: String): String =
    Normalizer.normalize(nombre.trim(), Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "")
        .lowercase()
        .replace(Regex("[^a-z0-9]+"), ".")
        .trim('.')

// Firebase Auth identifica por correo: si el usuario escribe solo un nombre,
// se convierte internamente en un correo. Si ya trae "@", se usa tal cual.
fun identificadorACorreo(identificador: String): String {
    val valor = identificador.trim()
    return if (valor.contains("@")) valor else normalizarNombre(valor) + "@" + DOMINIO_INTERNO
}

class UsuarioRepository(private val context: Context) {

    private val auth: FirebaseAuth get() = FirebaseAuth.getInstance()

    private fun verificarConexion() {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork)
        if (caps == null || !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            throw ConexionException()
        }
    }

    suspend fun iniciarSesion(identificador: String, password: String): Usuario {
        verificarConexion()
        try {
            val resultado = auth.signInWithEmailAndPassword(identificadorACorreo(identificador), password).await()
            return (resultado.user ?: throw CredencialesException()).aUsuario()
        } catch (e: Exception) {
            throw traducir(e)
        }
    }

    suspend fun registrar(nombre: String, password: String): Usuario {
        verificarConexion()
        try {
            val resultado = auth.createUserWithEmailAndPassword(identificadorACorreo(nombre), password).await()
            val user = resultado.user ?: throw CredencialesException()
            // Guarda el nombre tal como lo escribió la persona
            runCatching {
                user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(nombre.trim()).build()).await()
            }
            return user.aUsuario(nombreRespaldo = nombre.trim())
        } catch (e: Exception) {
            throw traducir(e)
        }
    }

    // Sirve para registrarse (cuenta nueva) o entrar (cuenta existente)
    suspend fun autenticarConGoogle(contexto: Context): Usuario {
        verificarConexion()
        val activity = contexto.buscarActividad()
        val webClientId = activity.getString(R.string.google_web_client_id)
        if (webClientId.isBlank() || webClientId.startsWith("REEMPLAZAR")) {
            throw GoogleException("Falta configurar google_web_client_id en strings.xml")
        }
        try {
            // 1) Botón "Iniciar sesión con Google". 2) Si no hay respuesta, selector clásico de cuentas.
            val credencial = try {
                pedirCredencial(activity, GetSignInWithGoogleOption.Builder(webClientId).build())
            } catch (e: NoCredentialException) {
                pedirCredencial(
                    activity,
                    GetGoogleIdOption.Builder()
                        .setServerClientId(webClientId)
                        .setFilterByAuthorizedAccounts(false)
                        .setAutoSelectEnabled(false)
                        .build()
                )
            }
            if (credencial !is CustomCredential ||
                credencial.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                throw GoogleException("No se pudo obtener la cuenta de Google")
            }
            val idToken = GoogleIdTokenCredential.createFrom(credencial.data).idToken
            val resultado = try {
                auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
            } catch (e: CancellationException) {
                throw e
            } catch (e: FirebaseNetworkException) {
                throw ConexionException()
            } catch (e: Exception) {
                // Antes esto se mostraba como "revisa los datos"; ahora se explica la causa real
                throw GoogleException(
                    "Firebase rechazó la cuenta de Google. Revisa que Google esté habilitado en " +
                        "Authentication (" + (e.message ?: e.javaClass.simpleName) + ")"
                )
            }
            return (resultado.user ?: throw GoogleException("Firebase no devolvió el usuario")).aUsuario()
        } catch (e: GetCredentialCancellationException) {
            throw GoogleException("Se canceló el acceso con Google")
        } catch (e: NoCredentialException) {
            // Sin cuentas en el dispositivo (o SHA-1 no registrada): abre Android para agregar una cuenta de Google
            runCatching {
                activity.startActivity(
                    Intent(Settings.ACTION_ADD_ACCOUNT)
                        .putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
            throw GoogleException(
                "No hay cuentas de Google disponibles. Agrega una cuenta y vuelve a pulsar el botón " +
                    "(si ya tienes una, registra la huella SHA-1 en Firebase)"
            )
        } catch (e: GetCredentialException) {
            throw GoogleException("No se pudo acceder con Google: " + (e.message ?: e.type))
        } catch (e: GoogleIdTokenParsingException) {
            throw GoogleException("Respuesta de Google no válida")
        } catch (e: Exception) {
            throw traducir(e)
        }
    }

    private suspend fun pedirCredencial(activity: Context, opcion: CredentialOption): Credential =
        CredentialManager.create(activity)
            .getCredential(activity, GetCredentialRequest.Builder().addCredentialOption(opcion).build())
            .credential

    // Credential Manager necesita la Activity; LocalContext puede venir envuelto en un ContextWrapper
    private fun Context.buscarActividad(): Context {
        var actual: Context = this
        while (actual is ContextWrapper) {
            if (actual is Activity) return actual
            actual = actual.baseContext
        }
        return this
    }

    // Solo funciona con correos reales (las cuentas creadas solo con nombre usan un correo interno)
    suspend fun restablecerPassword(correo: String) {
        verificarConexion()
        try {
            auth.sendPasswordResetEmail(correo.trim()).await()
        } catch (e: Exception) {
            throw traducir(e)
        }
    }

    fun cerrarSesion() {
        auth.signOut()
    }

    private fun FirebaseUser.aUsuario(nombreRespaldo: String? = null): Usuario {
        val correo = email.orEmpty()
        val nombre = displayName?.takeIf { it.isNotBlank() }
            ?: nombreRespaldo
            ?: correo.substringBefore("@")
        return Usuario(email = correo, nombre = nombre, rol = Rol.VENDEDOR)
    }

    private fun traducir(e: Exception): Exception = when (e) {
        is CancellationException -> e
        is FirebaseNetworkException -> ConexionException()
        is FirebaseAuthInvalidUserException,
        is FirebaseAuthInvalidCredentialsException -> CredencialesException()
        is FirebaseAuthUserCollisionException -> UsuarioExistenteException()
        else -> e
    }
}