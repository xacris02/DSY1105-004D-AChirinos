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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insercodechirinosestobar.data.local.*
import com.example.insercodechirinosestobar.model.*
import com.example.insercodechirinosestobar.ui.components.*
import com.example.insercodechirinosestobar.ui.theme.InsertColors

// Ruta GPS (coordenadas simuladas). Distribuidor: sus entregas + las sin asignar, y actualiza estados.
// Supervisor: todas las entregas, solo seguimiento.
@Composable
fun RutaScreen(
    rol: Rol,
    email: String,
    datos: OperacionDatos,
    onActualizar: (EntregaEntity, EstadoEntrega) -> Unit
) {
    val entregas = if (rol == Rol.REPARTIDOR) {
        datos.entregas.filter { it.repartidor.isBlank() || it.repartidor.equals(email, ignoreCase = true) }
    } else datos.entregas

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TituloPantalla(
                "Ruta GPS de Despacho",
                if (rol == Rol.REPARTIDOR) "Entregas asignadas en terreno (coordenadas simuladas)"
                else "Seguimiento de entregas en terreno (coordenadas simuladas)"
            )
        }
        item { MapaRuta(entregas) }
        item { Text("Secuencia de Entregas", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 6.dp)) }
        if (entregas.isEmpty()) {
            item { Text("No tienes entregas asignadas", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(entregas, key = { it.id }) { e ->
            val estado = e.estadoEntrega
            TarjetaApp {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(34.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) { Text(e.orden.toString(), fontWeight = FontWeight.Bold) }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(e.clienteNombre, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(e.direccion, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.width(8.dp))
                    Etiqueta(estado.etiqueta.uppercase(), estado.color())
                }
                if (rol == Rol.REPARTIDOR && estado != EstadoEntrega.ENTREGADO) {
                    Spacer(Modifier.height(10.dp))
                    if (estado == EstadoEntrega.PENDIENTE) {
                        Button(
                            onClick = { onActualizar(e, EstadoEntrega.EN_RUTA) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Iniciar ruta")
                        }
                    } else {
                        Button(
                            onClick = { onActualizar(e, EstadoEntrega.ENTREGADO) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Marcar como entregado")
                        }
                    }
                }
            }
        }
    }
}

// Mapa simulado: cuadrícula, línea punteada Base -> entregas y un punto por entrega
@Composable
private fun MapaRuta(entregas: List<EntregaEntity>) {
    val grilla = MaterialTheme.colorScheme.outlineVariant
    val linea = MaterialTheme.colorScheme.primary
    val puntos = entregas.sortedBy { it.orden }

    TarjetaApp {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Place, contentDescription = null, tint = InsertColors.Coral, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text("Mapa de Ruta Interactiva", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Punto(InsertColors.Verde)
            Spacer(Modifier.width(6.dp))
            Text("GPS Activo", fontSize = 12.sp, color = InsertColors.Verde)
        }
        Spacer(Modifier.height(10.dp))
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            val ancho = maxWidth
            val alto = maxHeight
            Canvas(modifier = Modifier.matchParentSize()) {
                for (i in 1..9) {
                    val x = size.width * i / 10f
                    drawLine(grilla, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
                }
                for (i in 1..5) {
                    val y = size.height * i / 6f
                    drawLine(grilla, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
                }
                val trazo = listOf(Offset(size.width * BASE_X, size.height * BASE_Y)) +
                    puntos.map { Offset(size.width * it.px, size.height * it.py) }
                for (i in 0 until trazo.size - 1) {
                    drawLine(
                        color = linea,
                        start = trazo[i],
                        end = trazo[i + 1],
                        strokeWidth = 5f,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f))
                    )
                }
            }
            NodoMapa("Base InsertCode", BASE_X, BASE_Y, InsertColors.Verde, ancho, alto)
            puntos.forEach { NodoMapa(it.clienteNombre, it.px, it.py, it.estadoEntrega.color(), ancho, alto) }
        }
    }
}

private const val BASE_X = 0.1f
private const val BASE_Y = 0.72f

@Composable
private fun NodoMapa(
    nombre: String, fx: Float, fy: Float, color: Color,
    ancho: androidx.compose.ui.unit.Dp, alto: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .offset(x = ancho * fx - 7.dp, y = alto * fy - 7.dp)
            .size(14.dp)
            .clip(CircleShape)
            .background(color)
            .border(2.dp, Color.White, CircleShape)
    )
    // La etiqueta se recorta si el nombre es largo, para no salirse del mapa
    Text(
        nombre,
        fontSize = 9.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .offset(x = (ancho * fx - 30.dp).coerceIn(0.dp, ancho - 70.dp), y = alto * fy + 9.dp)
            .width(70.dp)
    )
}
