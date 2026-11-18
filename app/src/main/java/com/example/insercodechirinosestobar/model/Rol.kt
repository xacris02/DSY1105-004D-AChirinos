package com.example.insercodechirinosestobar.model

// Roles de la app. Cada rol ve pantallas distintas (ver navigation/MenuInferior.kt)
// y tiene permisos distintos (lista "permisos", que se muestra en el Perfil).
enum class Rol(val titulo: String, val descripcion: String, val permisos: List<String>) {
    ADMIN(
        "Administrador",
        "Gestiona usuarios de prueba, roles y permisos",
        listOf(
            "Crear y editar usuarios de prueba",
            "Asignar roles y activar/desactivar usuarios",
            "Consultar roles y permisos del sistema",
            "Ver resumen general de la operación"
        )
    ),
    SUPERVISOR(
        "Supervisor",
        "Revisa indicadores comerciales y logísticos y hace seguimiento",
        listOf(
            "Consulta de indicadores comerciales y logísticos",
            "Consulta de estados de pedidos (solo lectura)",
            "Seguimiento de entregas y rutas (solo lectura)"
        )
    ),
    VENDEDOR(
        "Vendedor",
        "Consulta clientes, productos, pedidos y ventas; registra información autorizada",
        listOf(
            "Consulta de catálogo de productos y clientes",
            "Registro y cotización de nuevos pedidos",
            "Confirmar o cancelar sus pedidos",
            "Consulta de ventas y estados de pedidos"
        )
    ),
    REPARTIDOR(
        "Distribuidor",
        "Consulta rutas y entregas asignadas y actualiza su estado",
        listOf(
            "Consulta de rutas y entregas asignadas",
            "Actualizar estado de la entrega (en ruta / entregado)",
            "Ver el mapa de ruta de despacho"
        )
    );

    companion object {
        fun desde(nombre: String?): Rol =
            values().firstOrNull { it.name == nombre } ?: VENDEDOR
    }
}
