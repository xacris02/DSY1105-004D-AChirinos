package com.example.insercodechirinosestobar.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sesion")

// Persistencia local limitada: solo el último correo, nunca la contraseña
class SesionRepository(private val context: Context) {

    private val claveEmail = stringPreferencesKey("ultimo_email")

    val emailGuardado: Flow<String> = context.dataStore.data.map { it[claveEmail] ?: "" }

    suspend fun guardarEmail(email: String) {
        context.dataStore.edit { it[claveEmail] = email }
    }

    suspend fun borrarEmail() {
        context.dataStore.edit { it.remove(claveEmail) }
    }
}
