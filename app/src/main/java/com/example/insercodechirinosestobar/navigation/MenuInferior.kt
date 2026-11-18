package com.example.insercodechirinosestobar.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.insercodechirinosestobar.model.Rol

data class ItemMenu(val ruta: String, val titulo: String, val icono: ImageVector)

private val dashboard = ItemMenu(AppRoutes.Dashboard.route, "Dashboard", Icons.Filled.Dashboard)
private val pedidos = ItemMenu(AppRoutes.Pedidos.route, "Pedidos", Icons.Filled.Inventory2)
private val catalogo = ItemMenu(AppRoutes.Catalogo.route, "Catálogo", Icons.Filled.Storefront)
private val clientes = ItemMenu(AppRoutes.Clientes.route, "Clientes", Icons.Filled.Business)
private val ruta = ItemMenu(AppRoutes.Ruta.route, "Ruta GPS", Icons.Filled.Map)
private val usuarios = ItemMenu(AppRoutes.Usuarios.route, "Usuarios", Icons.Filled.AdminPanelSettings)
private val perfil = ItemMenu(AppRoutes.Perfil.route, "Perfil", Icons.Filled.Person)

// Cada rol ve un menú distinto (la primera opción es su pantalla de inicio)
fun itemsMenuPara(rol: Rol): List<ItemMenu> = when (rol) {
    Rol.ADMIN -> listOf(usuarios, dashboard, perfil)
    Rol.SUPERVISOR -> listOf(dashboard, pedidos, ruta, perfil)
    Rol.VENDEDOR -> listOf(dashboard, pedidos, catalogo, clientes, perfil)
    Rol.REPARTIDOR -> listOf(ruta, perfil)
}

fun rutaInicio(rol: Rol): String = itemsMenuPara(rol).first().ruta

// Todas las rutas que son pestañas (para saber cuándo mostrar la barra superior e inferior)
val rutasPestana: Set<String> = listOf(
    AppRoutes.Dashboard, AppRoutes.Pedidos, AppRoutes.Catalogo, AppRoutes.Clientes,
    AppRoutes.Ruta, AppRoutes.Usuarios, AppRoutes.Perfil
).map { it.route }.toSet()
