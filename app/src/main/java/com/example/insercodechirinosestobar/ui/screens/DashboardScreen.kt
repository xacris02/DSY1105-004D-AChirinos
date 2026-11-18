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

// Indicadores según el rol: Administrador (usuarios), Supervisor (comercial + logístico), Vendedor (ventas)
@Composable
fun DashboardScreen(rol: Rol, email: String, datos: OperacionDatos) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            when (rol) {
                Rol.ADMIN -> TituloPantalla("Panel de Administración", "Resumen de usuarios de prueba y operación")
                Rol.SUPERVISOR -> TituloPantalla("Indicadores", "Seguimiento comercial y logístico de la operación")
                else -> TituloPantalla("Mis Ventas", "Resumen comercial de tu gestión")
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                if (rol == Rol.ADMIN) {
                    Kpi("Usuarios activos", datos.usuarios.count { it.activo }.toString(), InsertColors.Teal, Modifier.weight(1f))
                    Kpi("Total usuarios", datos.usuarios.size.toString(), InsertColors.Lila, Modifier.weight(1f))
                } else {
                    Kpi("Ventas", formatoPesos(datos.totalVentas), InsertColors.Verde, Modifier.weight(1f))
                    Kpi("Pedidos", datos.pedidos.size.toString(), InsertColors.Teal, Modifier.weight(1f))
                }
            }
        }
        if (rol == Rol.VENDEDOR) {
            item {
                val propios = datos.pedidos.filter { it.vendedor.equals(email, ignoreCase = true) }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Kpi("Mis pedidos", propios.size.toString(), InsertColors.Ambar, Modifier.weight(1f))
                    Kpi("Clientes", datos.clientes.size.toString(), InsertColors.Celeste, Modifier.weight(1f))
                }
            }
        }
        if (rol == Rol.ADMIN) {
            item {
                TarjetaApp {
                    Text("Usuarios por rol", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Rol.values().forEach { r ->
                        BarraEstado(r.titulo, datos.usuarios.count { it.rol == r.name }, datos.usuarios.size, r.color())
                    }
                }
            }
        }
        if (rol != Rol.ADMIN) {
            item {
                TarjetaApp {
                    Text("Pedidos por estado", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    EstadoPedido.values().forEach { e ->
                        BarraEstado(e.etiqueta, datos.pedidos.count { it.estado == e.name }, datos.pedidos.size, e.color())
                    }
                }
            }
        }
        if (rol == Rol.SUPERVISOR) {
            item {
                TarjetaApp {
                    Text("Entregas (logística)", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    EstadoEntrega.values().forEach { e ->
                        BarraEstado(e.etiqueta, datos.entregas.count { it.estado == e.name }, datos.entregas.size, e.color())
                    }
                    val hechas = datos.entregas.count { it.estado == EstadoEntrega.ENTREGADO.name }
                    val pct = if (datos.entregas.isEmpty()) 0 else hechas * 100 / datos.entregas.size
                    Spacer(Modifier.height(6.dp))
                    Text("Cumplimiento de entregas: $pct%", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
            }
        }
        if (rol == Rol.ADMIN) {
            item {
                TarjetaApp {
                    Text("Roles del sistema", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Rol.values().forEach { r ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            Etiqueta(r.titulo.uppercase(), r.color())
                            Spacer(Modifier.width(10.dp))
                            Text(r.descripcion, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Kpi(titulo: String, valor: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(titulo, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(valor, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = color, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun BarraEstado(etiqueta: String, cantidad: Int, total: Int, color: Color) {
    val fraccion = if (total == 0) 0f else cantidad.toFloat() / total
    Column(modifier = Modifier.padding(vertical = 5.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(etiqueta, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text(cantidad.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
            Box(modifier = Modifier.fillMaxWidth(fraccion).height(8.dp).clip(RoundedCornerShape(4.dp)).background(color))
        }
    }
}
