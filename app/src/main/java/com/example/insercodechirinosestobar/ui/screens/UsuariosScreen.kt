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

// Administrador: gestiona usuarios de prueba, roles y permisos
@Composable
fun UsuariosScreen(
    emailActual: String,
    datos: OperacionDatos,
    onCambiarRol: (UsuarioEntity, Rol) -> Unit,
    onAlternarActivo: (UsuarioEntity) -> Unit,
    onCrear: (String, String, Rol) -> Unit
) {
    var mostrarDialogo by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { TituloPantalla("Usuarios de Prueba", "Gestión de usuarios, roles y permisos") }
        item {
            Button(onClick = { mostrarDialogo = true }, shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Nuevo usuario de prueba")
            }
        }
        items(datos.usuarios, key = { it.email }) { u ->
            FilaUsuario(
                usuario = u,
                esYo = u.email.equals(emailActual, ignoreCase = true),
                onCambiarRol = onCambiarRol,
                onAlternarActivo = onAlternarActivo
            )
        }
        item { Text("Roles y permisos", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp)) }
        items(Rol.values().toList()) { r ->
            TarjetaApp {
                Etiqueta(r.titulo.uppercase(), r.color())
                Spacer(Modifier.height(6.dp))
                Text(r.descripcion, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(6.dp))
                r.permisos.forEach { Text("•  $it", fontSize = 13.sp, modifier = Modifier.padding(vertical = 1.dp)) }
            }
        }
    }

    if (mostrarDialogo) {
        DialogoNuevoUsuario(
            onCerrar = { mostrarDialogo = false },
            onCrear = { email, nombre, rol ->
                onCrear(email, nombre, rol)
                mostrarDialogo = false
            }
        )
    }
}

@Composable
private fun FilaUsuario(
    usuario: UsuarioEntity,
    esYo: Boolean,
    onCambiarRol: (UsuarioEntity, Rol) -> Unit,
    onAlternarActivo: (UsuarioEntity) -> Unit
) {
    var menu by remember { mutableStateOf(false) }
    val rol = usuario.rolUsuario

    TarjetaApp {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconoCuadro(Icons.Filled.Person, rol.color(), 40)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(usuario.nombre + if (esYo) " (tú)" else "", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(usuario.email, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Switch(checked = usuario.activo, onCheckedChange = { onAlternarActivo(usuario) }, enabled = !esYo)
        }
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Etiqueta(rol.titulo.uppercase(), rol.color())
            Spacer(Modifier.width(8.dp))
            Etiqueta(if (usuario.activo) "ACTIVO" else "INACTIVO", if (usuario.activo) InsertColors.Verde else InsertColors.Coral)
            Spacer(Modifier.weight(1f))
            Box {
                TextButton(onClick = { menu = true }, enabled = !esYo) { Text("Cambiar rol") }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    Rol.values().forEach { r ->
                        DropdownMenuItem(
                            text = { Text(r.titulo) },
                            onClick = {
                                menu = false
                                onCambiarRol(usuario, r)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogoNuevoUsuario(onCerrar: () -> Unit, onCrear: (String, String, Rol) -> Unit) {
    var email by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var rol by remember { mutableStateOf(Rol.VENDEDOR) }
    val emailValido = email.trim().contains("@") && email.trim().length > 4

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Nuevo usuario de prueba") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Correo") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Text("Rol", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Rol.values().forEach { r ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable { rol = r }) {
                        RadioButton(selected = rol == r, onClick = { rol = r })
                        Text(r.titulo)
                    }
                }
            }
        },
        confirmButton = { TextButton(enabled = emailValido, onClick = { onCrear(email, nombre, rol) }) { Text("Crear") } },
        dismissButton = { TextButton(onClick = onCerrar) { Text("Cancelar") } }
    )
}
