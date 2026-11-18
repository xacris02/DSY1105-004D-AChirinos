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

// Perfil: datos de la cuenta, permisos del rol, ajustes y cerrar sesión
@Composable
fun PerfilScreen(
    nombre: String,
    email: String,
    rol: Rol,
    notificaciones: Boolean,
    modoOscuro: Boolean,
    onNotificaciones: (Boolean) -> Unit,
    onModoOscuro: (Boolean) -> Unit,
    onVerPosts: () -> Unit,
    onSalir: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { TituloPantalla("Mi Perfil Corporativo", "Información de cuenta Firebase, permisos asignados y preferencias") }
        item {
            TarjetaApp {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(84.dp).clip(RoundedCornerShape(24.dp)).background(rol.color().copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Work, contentDescription = null, tint = rol.color(), modifier = Modifier.size(42.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(nombre.ifBlank { rol.titulo }, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f, fill = false))
                        Spacer(Modifier.width(8.dp))
                        Etiqueta(rol.titulo.uppercase(), rol.color())
                    }
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Email, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.width(6.dp))
                        Text(email, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(rol.descripcion, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
                    Spacer(Modifier.height(14.dp))
                    Etiqueta("Firebase Auth Verificado", InsertColors.Verde)
                    Text("Sesión cifrada de extremo a extremo", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }
        item {
            TarjetaApp {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Permisos del Perfil (${rol.titulo.uppercase()})", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(10.dp))
                rol.permisos.forEach { permiso ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text(permiso, fontSize = 13.sp, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = InsertColors.Verde, modifier = Modifier.size(16.dp))
                        Text(" Activo", fontSize = 12.sp, color = InsertColors.Verde, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        item {
            TarjetaApp {
                Text("Preferencias", fontWeight = FontWeight.Bold)
                FilaSwitch("Notificaciones push", notificaciones, onNotificaciones)
                FilaSwitch("Modo oscuro", modoOscuro, onModoOscuro)
            }
        }
        item {
            OutlinedButton(onClick = onVerPosts, shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                Text("Ver posts (API REST)")
            }
        }
        item {
            Button(
                onClick = onSalir,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Cerrar sesión", fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun FilaSwitch(texto: String, valor: Boolean, onCambio: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(texto, modifier = Modifier.weight(1f))
        Switch(checked = valor, onCheckedChange = onCambio)
    }
}
