package com.example.insercodechirinosestobar.model

import com.example.insercodechirinosestobar.data.local.ClienteEntity
import com.example.insercodechirinosestobar.data.local.EntregaEntity
import com.example.insercodechirinosestobar.data.local.PedidoEntity
import com.example.insercodechirinosestobar.data.local.ProductoEntity
import com.example.insercodechirinosestobar.data.local.UsuarioEntity

enum class EstadoPedido(val etiqueta: String) {
    COTIZADO("Cotizado"),
    CONFIRMADO("Confirmado"),
    EN_DESPACHO("En despacho"),
    ENTREGADO("Entregado"),
    CANCELADO("Cancelado")
}

enum class EstadoEntrega(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    EN_RUTA("En ruta"),
    ENTREGADO("Entregado")
}

val PedidoEntity.estadoPedido: EstadoPedido
    get() = runCatching { EstadoPedido.valueOf(estado) }.getOrDefault(EstadoPedido.COTIZADO)

val EntregaEntity.estadoEntrega: EstadoEntrega
    get() = runCatching { EstadoEntrega.valueOf(estado) }.getOrDefault(EstadoEntrega.PENDIENTE)

val UsuarioEntity.rolUsuario: Rol get() = Rol.desde(rol)

// Todo lo que la UI necesita, emitido de nuevo cada vez que cambia una tabla de Room
data class OperacionDatos(
    val productos: List<ProductoEntity> = emptyList(),
    val clientes: List<ClienteEntity> = emptyList(),
    val pedidos: List<PedidoEntity> = emptyList(),
    val entregas: List<EntregaEntity> = emptyList(),
    val usuarios: List<UsuarioEntity> = emptyList()
) {
    // Ventas = pedidos confirmados, en despacho o entregados
    val ventas: List<PedidoEntity>
        get() = pedidos.filter {
            it.estadoPedido in setOf(EstadoPedido.CONFIRMADO, EstadoPedido.EN_DESPACHO, EstadoPedido.ENTREGADO)
        }
    val totalVentas: Int get() = ventas.sumOf { it.total }
}

fun formatoPesos(monto: Int): String =
    "$" + monto.toString().reversed().chunked(3).joinToString(".").reversed()
