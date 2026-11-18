package com.example.insercodechirinosestobar.model

// Firebase no guarda roles: la base local (tabla "usuarios") guarda el rol de cada usuario de prueba.
// Si el correo aún no existe se deduce por prefijo y se guarda:
// admin... -> Administrador, supervisor... -> Supervisor, repartidor.../distribuidor... -> Distribuidor,
// el resto -> Vendedor
fun rolSegunCorreo(correo: String): Rol {
    val usuario = correo.substringBefore("@").lowercase()
    return when {
        usuario.startsWith("admin") -> Rol.ADMIN
        usuario.startsWith("supervisor") -> Rol.SUPERVISOR
        usuario.startsWith("repartidor") || usuario.startsWith("distribuidor") -> Rol.REPARTIDOR
        else -> Rol.VENDEDOR
    }
}
