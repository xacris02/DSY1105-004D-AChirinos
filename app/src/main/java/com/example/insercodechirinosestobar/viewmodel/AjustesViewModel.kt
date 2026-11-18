package com.example.insercodechirinosestobar.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Ajustes(
    val verificacion2Pasos: Boolean = true,
    val notificacionesPush: Boolean = true,
    val alertasCriticas: Boolean = true,
    val modoOscuro: Boolean = true
)

// Preferencias del Perfil (interruptores). Se guardan en el teléfono con SharedPreferences.
class AjustesViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("ajustes", Context.MODE_PRIVATE)

    private val _ajustes = MutableStateFlow(
        Ajustes(
            verificacion2Pasos = prefs.getBoolean("2pasos", true),
            notificacionesPush = prefs.getBoolean("push", true),
            alertasCriticas = prefs.getBoolean("criticas", true),
            modoOscuro = prefs.getBoolean("oscuro", true)
        )
    )
    val ajustes: StateFlow<Ajustes> = _ajustes.asStateFlow()

    fun cambiar2Pasos(v: Boolean) = guardar("2pasos", v) { it.copy(verificacion2Pasos = v) }
    fun cambiarPush(v: Boolean) = guardar("push", v) { it.copy(notificacionesPush = v) }
    fun cambiarCriticas(v: Boolean) = guardar("criticas", v) { it.copy(alertasCriticas = v) }
    fun cambiarModoOscuro(v: Boolean) = guardar("oscuro", v) { it.copy(modoOscuro = v) }

    private fun guardar(clave: String, valor: Boolean, cambio: (Ajustes) -> Ajustes) {
        prefs.edit().putBoolean(clave, valor).apply()
        _ajustes.value = cambio(_ajustes.value)
    }
}
