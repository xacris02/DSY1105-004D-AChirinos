package com.example.insercodechirinosestobar.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.insercodechirinosestobar.model.Rol
import com.example.insercodechirinosestobar.model.Usuario
import kotlinx.coroutines.delay

class CredencialesException : Exception()
class ConexionException : Exception()

class UsuarioRepository(private val context: Context) {

    private val usuarios = listOf(
        Usuario("admin@guardian.test", "123456", Rol.ADMIN),
        Usuario("supervisor@guardian.test", "123456", Rol.SUPERVISOR),
        Usuario("operador@guardian.test", "123456", Rol.OPERADOR)
    )

    private fun hayConexion(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun autenticar(email: String, password: String): Usuario {
        delay(1500) // simula la espera del servidor
        if (!hayConexion()) throw ConexionException()
        return usuarios.firstOrNull {
            it.email.equals(email.trim(), ignoreCase = true) && it.password == password
        } ?: throw CredencialesException()
    }
}
