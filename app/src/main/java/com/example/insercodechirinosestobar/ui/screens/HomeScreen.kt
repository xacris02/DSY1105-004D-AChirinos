package com.example.insercodechirinosestobar.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.insercodechirinosestobar.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: LoginViewModel,
    onSinSesion: () -> Unit
) {
    // Mismo LoginViewModel que el Login: de aquí se obtiene el rol
    val estado by viewModel.uiState.collectAsState()
    val rol = estado.rol

    // Si no hay rol (sesión cerrada) se vuelve al Login
    LaunchedEffect(rol) {
        if (rol == null) onSinSesion()
    }
    if (rol == null) return

    Scaffold(
        topBar = { TopAppBar(title = { Text("Inicio") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Bienvenido, " + rol.titulo,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(text = estado.email)
            Text(text = rol.descripcion)

            Button(
                onClick = viewModel::cerrarSesion,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
