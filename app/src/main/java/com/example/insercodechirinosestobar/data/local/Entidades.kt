package com.example.insercodechirinosestobar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidades de Room (datos ficticios de demostración)

@Entity(tableName = "productos")
data class ProductoEntity(
    @PrimaryKey val codigo: String,
    val categoria: String,
    val nombre: String,
    val descripcion: String,
    val precio: Int
)

@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val contacto: String
)

@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clienteId: Int,
    val clienteNombre: String,
    val productoCodigo: String,
    val productoNombre: String,
    val cantidad: Int,
    val total: Int,
    val estado: String,
    val vendedor: String,
    val fecha: String
)

@Entity(tableName = "entregas")
data class EntregaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pedidoId: Long,
    val orden: Int,
    val clienteNombre: String,
    val direccion: String,
    val estado: String,
    val repartidor: String,   // correo del distribuidor ("" = sin asignar)
    val px: Float,            // posición simulada en el mapa (0..1)
    val py: Float
)

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val email: String,
    val nombre: String,
    val rol: String,
    val activo: Boolean
)
