package com.example.insercodechirinosestobar.navigation

sealed class AppRoutes(val route: String) {
    object Login : AppRoutes("login")
    object Registro : AppRoutes("registro")

    // Pestañas del menú inferior (cada rol ve solo algunas)
    object Dashboard : AppRoutes("dashboard")
    object Pedidos : AppRoutes("pedidos")
    object Catalogo : AppRoutes("catalogo")
    object Clientes : AppRoutes("clientes")
    object Ruta : AppRoutes("ruta")
    object Usuarios : AppRoutes("usuarios")
    object Perfil : AppRoutes("perfil")

    object Posts : AppRoutes("posts")
}
