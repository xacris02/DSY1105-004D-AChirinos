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

// Catálogo de productos. El Vendedor cotiza: elige cliente y cantidad y se registra un pedido "Cotizado".
@Composable
fun CatalogoScreen(
    email: String,
    datos: OperacionDatos,
    onCotizar: (ClienteEntity, ProductoEntity, Int) -> Unit
) {
    var seleccionado by remember { mutableStateOf<ProductoEntity?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { TituloPantalla("Catálogo de Soluciones", "Productos y licencias InsertCode disponibles para cotización") }
        items(datos.productos, key = { it.codigo }) { p ->
            val color = if (p.categoria == "SOFTWARE") InsertColors.Teal else InsertColors.Ambar
            TarjetaApp {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Etiqueta(p.categoria, color)
                    Spacer(Modifier.weight(1f))
                    Text(p.codigo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(Modifier.height(8.dp))
                Text(p.nombre, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(p.descripcion, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(formatoPesos(p.precio), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = InsertColors.Verde, modifier = Modifier.weight(1f))
                    Button(onClick = { seleccionado = p }, shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Cotizar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    seleccionado?.let { producto ->
        DialogoCotizar(
            producto = producto,
            clientes = datos.clientes,
            onCerrar = { seleccionado = null },
            onConfirmar = { cliente, cantidad ->
                onCotizar(cliente, producto, cantidad)
                seleccionado = null
            }
        )
    }
}

@Composable
private fun DialogoCotizar(
    producto: ProductoEntity,
    clientes: List<ClienteEntity>,
    onCerrar: () -> Unit,
    onConfirmar: (ClienteEntity, Int) -> Unit
) {
    var clienteId by remember { mutableStateOf(clientes.firstOrNull()?.id) }
    var cantidad by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Cotizar ${producto.nombre}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Cliente", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                clientes.forEach { c ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().clickable { clienteId = c.id }
                    ) {
                        RadioButton(selected = clienteId == c.id, onClick = { clienteId = c.id })
                        Text(c.nombre, fontSize = 14.sp)
                    }
                }
                Text("Cantidad", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (cantidad > 1) cantidad-- }) { Icon(Icons.Filled.Remove, contentDescription = "Menos") }
                    Text(cantidad.toString(), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { if (cantidad < 99) cantidad++ }) { Icon(Icons.Filled.Add, contentDescription = "Más") }
                }
                Text("Total: " + formatoPesos(producto.precio * cantidad), fontWeight = FontWeight.ExtraBold, color = InsertColors.Verde)
            }
        },
        confirmButton = {
            TextButton(
                enabled = clienteId != null,
                onClick = { clientes.find { it.id == clienteId }?.let { onConfirmar(it, cantidad) } }
            ) { Text("Registrar cotización") }
        },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}
