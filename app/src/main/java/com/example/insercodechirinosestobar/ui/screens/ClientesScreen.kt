package com.example.insercodechirinosestobar.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insercodechirinosestobar.data.local.*
import com.example.insercodechirinosestobar.model.*
import com.example.insercodechirinosestobar.ui.components.*
import com.example.insercodechirinosestobar.ui.theme.InsertColors

// Clientes (consulta). Muestra cuántos pedidos y cuánto se les ha vendido.
@Composable
fun ClientesScreen(datos: OperacionDatos) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { TituloPantalla("Clientes", "Cartera de clientes y su historial de compra") }
        items(datos.clientes, key = { it.id }) { c ->
            val suyos = datos.pedidos.filter { it.clienteId == c.id }
            val vendido = datos.ventas.filter { it.clienteId == c.id }.sumOf { it.total }
            TarjetaApp {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconoCuadro(Icons.Filled.Business, InsertColors.Teal)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(c.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(c.contacto, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Text(c.direccion, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(c.telefono, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Etiqueta("${suyos.size} pedidos", InsertColors.Celeste)
                    Etiqueta("Vendido ${formatoPesos(vendido)}", InsertColors.Verde)
                }
            }
        }
    }
}
