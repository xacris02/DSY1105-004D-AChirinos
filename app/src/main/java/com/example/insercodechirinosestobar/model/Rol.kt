package com.example.insercodechirinosestobar.model

enum class Rol(val titulo: String, val descripcion: String) {
    ADMIN("Administrador", "Gestiona usuarios, roles y permisos"),
    SUPERVISOR("Supervisor", "Revisa indicadores y hace seguimiento"),
    OPERADOR("Operador", "Consulta y actualiza tareas asignadas")
}
