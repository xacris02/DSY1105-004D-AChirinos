package com.example.insercodechirinosestobar.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.insercodechirinosestobar.R
import com.example.insercodechirinosestobar.ui.theme.InserCodeTheme

// Pantalla de inicio: logo + Iniciar sesión / Registrarse
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BienvenidaScreen(
    onIniciarSesion: () -> Unit,
    onRegistrarse: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Modo Guardián") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Modo Guardián",
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(8.dp),
                contentScale = ContentScale.Fit
            )
            Button(
                onClick = onIniciarSesion,
                modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth()
            ) {
                Text("Iniciar sesión")
            }
            OutlinedButton(
                onClick = onRegistrarse,
                modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewBienvenida() {
    InserCodeTheme { BienvenidaScreen({}, {}) }
}
