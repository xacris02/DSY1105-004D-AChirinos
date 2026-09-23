package com.example.insercodechirinosestobar.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.insercodechirinosestobar.R

@Composable
fun HomeScreenExpandida() {
    Row(modifier = Modifier.fillMaxSize()) {
        // Panel lateral
        Surface(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo de la app",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.Fit
                )
                Text("Menú", style = MaterialTheme.typography.titleLarge)
                Text("Pedidos")
                Text("Rutas")
                Text("Entregas")
            }
        }
        // Contenido principal
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "¡Bienvenido a INSER CODE!",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.displaySmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf("Ventas", "Pedidos", "Entregas").forEach { titulo ->
                    Card(modifier = Modifier.weight(1f)) {
                        Text(
                            text = titulo,
                            modifier = Modifier.padding(24.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            Button(onClick = { /* acción futura */ }) {
                Text("Presioname")
            }
        }
    }
}

@Preview(name = "Expanded", widthDp = 1100, heightDp = 800)
@Composable
fun PreviewExpanded() {
    HomeScreenExpandida()
}