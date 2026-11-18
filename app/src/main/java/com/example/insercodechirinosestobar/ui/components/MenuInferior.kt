package com.example.insercodechirinosestobar.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.insercodechirinosestobar.model.Rol
import com.example.insercodechirinosestobar.navigation.itemsMenuPara

// Menú inferior: las opciones dependen del rol
@Composable
fun MenuInferior(
    rol: Rol,
    rutaActual: String?,
    onSeleccionar: (String) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        itemsMenuPara(rol).forEach { item ->
            val seleccionado = rutaActual == item.ruta
            NavigationBarItem(
                selected = seleccionado,
                onClick = { if (!seleccionado) onSeleccionar(item.ruta) },
                icon = { Icon(item.icono, contentDescription = item.titulo) },
                label = { Text(item.titulo, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
