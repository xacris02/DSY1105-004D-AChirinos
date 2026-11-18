package com.example.insercodechirinosestobar.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.insercodechirinosestobar.model.EstadoEntrega
import com.example.insercodechirinosestobar.model.EstadoPedido
import com.example.insercodechirinosestobar.model.Rol
import com.example.insercodechirinosestobar.ui.theme.InsertColors

fun Rol.color(): Color = when (this) {
    Rol.ADMIN -> InsertColors.Coral
    Rol.SUPERVISOR -> InsertColors.Lila
    Rol.VENDEDOR -> InsertColors.Teal
    Rol.REPARTIDOR -> InsertColors.Ambar
}

fun EstadoPedido.color(): Color = when (this) {
    EstadoPedido.COTIZADO -> InsertColors.Ambar
    EstadoPedido.CONFIRMADO -> InsertColors.Teal
    EstadoPedido.EN_DESPACHO -> InsertColors.Celeste
    EstadoPedido.ENTREGADO -> InsertColors.Verde
    EstadoPedido.CANCELADO -> InsertColors.Coral
}

fun EstadoEntrega.color(): Color = when (this) {
    EstadoEntrega.PENDIENTE -> InsertColors.Ambar
    EstadoEntrega.EN_RUTA -> InsertColors.Celeste
    EstadoEntrega.ENTREGADO -> InsertColors.Verde
}

// Tarjeta con borde suave
@Composable
fun TarjetaApp(modifier: Modifier = Modifier, contenido: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) { contenido() }
    }
}

// Chip de texto (SOFTWARE, PENDIENTE, VENDEDOR...)
@Composable
fun Etiqueta(texto: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.45f))
    ) {
        Text(
            text = texto,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun IconoCuadro(icono: ImageVector, color: Color, tamano: Int = 44) {
    Box(
        modifier = Modifier
            .size(tamano.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size((tamano * 0.5f).dp))
    }
}

@Composable
fun Punto(color: Color, tamano: Int = 8) {
    Box(modifier = Modifier.size(tamano.dp).clip(CircleShape).background(color))
}

// Barra superior de la app: logo "</>", InsertCode, chip del rol y botón de salir
@Composable
fun EncabezadoApp(rol: Rol, onSalir: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).statusBarsPadding()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text("</>", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 15.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("InsertCode", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = MaterialTheme.colorScheme.onBackground)
                    Spacer(Modifier.width(8.dp))
                    Etiqueta(rol.titulo.uppercase(), rol.color())
                }
                Text("Ventas & Distribución Móvil", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            }
            IconButton(onClick = onSalir) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar sesión", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

// Título + subtítulo de cada pantalla
@Composable
fun TituloPantalla(titulo: String, subtitulo: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(titulo, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground)
        Text(subtitulo, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
