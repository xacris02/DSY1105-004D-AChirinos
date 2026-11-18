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

// Pedidos y estados. Solo el Vendedor puede confirmar/cancelar sus pedidos cotizados;
// el Supervisor solo consulta.
@Composable
fun PedidosScreen(
    rol: Rol,
    email: String,
    datos: OperacionDatos,
    onCambiarEstado: (PedidoEntity, EstadoPedido) -> Unit
) {
    var filtro by remember { mutableStateOf<EstadoPedido?>(null) }
    val lista = datos.pedidos.filter { filtro == null || it.estado == filtro?.name }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TituloPantalla(
                "Pedidos",
                if (rol == Rol.SUPERVISOR) "Estados de pedidos (solo lectura)" else "Cotizaciones, ventas y estados"
            )
        }
        item {
            Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ChipFiltro("Todos", MaterialTheme.colorScheme.primary, filtro == null) { filtro = null }
                EstadoPedido.values().forEach { e ->
                    ChipFiltro(e.etiqueta, e.color(), filtro == e) { filtro = e }
                }
            }
        }
        if (lista.isEmpty()) {
            item { Text("No hay pedidos con este estado", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(lista, key = { it.id }) { p ->
            val estado = p.estadoPedido
            val esPropio = p.vendedor.equals(email, ignoreCase = true)
            TarjetaApp {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("PED-" + p.id.toString().padStart(3, '0'), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.weight(1f))
                    Etiqueta(estado.etiqueta.uppercase(), estado.color())
                }
                Spacer(Modifier.height(6.dp))
                Text(p.clienteNombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("${p.cantidad} x ${p.productoNombre}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(formatoPesos(p.total), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = InsertColors.Verde, modifier = Modifier.weight(1f))
                    Text(p.fecha, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (rol == Rol.VENDEDOR && esPropio && estado == EstadoPedido.COTIZADO) {
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = { onCambiarEstado(p, EstadoPedido.CONFIRMADO) }, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                            Text("Confirmar")
                        }
                        OutlinedButton(onClick = { onCambiarEstado(p, EstadoPedido.CANCELADO) }, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                            Text("Cancelar", color = InsertColors.Coral)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipFiltro(texto: String, color: Color, activo: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (activo) color.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface)
            .border(1.dp, if (activo) color else MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(texto, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = if (activo) color else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
