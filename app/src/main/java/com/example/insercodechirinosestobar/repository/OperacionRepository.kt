package com.example.insercodechirinosestobar.repository

import android.content.Context
import com.example.insercodechirinosestobar.data.local.AppDatabase
import com.example.insercodechirinosestobar.data.local.ClienteEntity
import com.example.insercodechirinosestobar.data.local.EntregaEntity
import com.example.insercodechirinosestobar.data.local.OperacionDao
import com.example.insercodechirinosestobar.data.local.PedidoEntity
import com.example.insercodechirinosestobar.data.local.ProductoEntity
import com.example.insercodechirinosestobar.data.local.UsuarioEntity
import com.example.insercodechirinosestobar.model.EstadoEntrega
import com.example.insercodechirinosestobar.model.EstadoPedido
import com.example.insercodechirinosestobar.model.OperacionDatos
import com.example.insercodechirinosestobar.model.Rol
import com.example.insercodechirinosestobar.model.rolSegunCorreo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Persistencia local con Room (SQLite). Solo datos ficticios, nada sensible.
class OperacionRepository(private val dao: OperacionDao) {

    constructor(context: Context) : this(AppDatabase.obtener(context).operacionDao())

    val datos: Flow<OperacionDatos> = combine(
        dao.observarProductos(),
        dao.observarClientes(),
        dao.observarPedidos(),
        dao.observarEntregas(),
        dao.observarUsuarios()
    ) { productos, clientes, pedidos, entregas, usuarios ->
        OperacionDatos(productos, clientes, pedidos, entregas, usuarios)
    }

    // ---------- Usuarios y roles ----------

    // Devuelve el rol del usuario, o null si el administrador lo desactivó.
    // Si el correo no existe todavía, se crea con el rol deducido del correo.
    suspend fun rolDe(email: String, nombre: String): Rol? {
        val correo = email.trim().lowercase()
        val existente = dao.usuarioPorEmail(correo)
        if (existente != null) {
            return if (existente.activo) Rol.desde(existente.rol) else null
        }
        val rol = rolSegunCorreo(correo)
        dao.insertarUsuarioSiNoExiste(
            UsuarioEntity(correo, nombre.ifBlank { correo.substringBefore("@") }, rol.name, true)
        )
        return rol
    }

    suspend fun guardarUsuario(usuario: UsuarioEntity) =
        dao.guardarUsuario(usuario.copy(email = usuario.email.trim().lowercase()))

    // ---------- Datos iniciales ----------

    suspend fun sembrar() {
        // Usuarios de prueba (IGNORE: no pisa cambios del administrador)
        listOf(
            UsuarioEntity("admin@insertcode.cl", "Administrador", Rol.ADMIN.name, true),
            UsuarioEntity("supervisor@insertcode.cl", "Supervisor Comercial", Rol.SUPERVISOR.name, true),
            UsuarioEntity("vendedor@insertcode.cl", "Vendedor Terreno", Rol.VENDEDOR.name, true),
            UsuarioEntity("repartidor@insertcode.cl", "Distribuidor Ruta Sur", Rol.REPARTIDOR.name, true)
        ).forEach { dao.insertarUsuarioSiNoExiste(it) }

        if (dao.contarProductos() > 0) return

        dao.insertarProductos(
            listOf(
                ProductoEntity("PROD-01", "SOFTWARE", "Licencia Dashboard Web Sales", "Sistema centralizado de ventas, inventario e indicadores.", 450000),
                ProductoEntity("PROD-02", "HARDWARE", "Módulo IoT Rastreador GPS", "Hardware de rastreo vehicular en tiempo real para flotas.", 120000),
                ProductoEntity("PROD-03", "HARDWARE", "Terminal POS Móvil Android", "Dispositivo táctil portátil con lector de comprobantes.", 180000),
                ProductoEntity("PROD-04", "SOFTWARE", "Soporte Premium Anual", "Mesa de ayuda 24/7 y actualizaciones por 12 meses.", 90000)
            )
        )
        dao.insertarClientes(
            listOf(
                ClienteEntity(1, "Duoc UC Sede Alameda", "Av. España 8, Santiago Centro", "+56 2 2360 0000", "Mesa de compras"),
                ClienteEntity(2, "Empresa Tech Chile Ltd", "Av. Providencia 1234, Of. 502", "+56 2 2111 2233", "Gerencia TI"),
                ClienteEntity(3, "Comercial Central InsertCode", "Av. Andrés Bello 2680, Las Condes", "+56 2 2444 5566", "Adquisiciones"),
                ClienteEntity(4, "Distribuidora Sur SpA", "Gran Avenida 5120, San Miguel", "+56 2 2777 8899", "Logística")
            )
        )

        val v = "vendedor@insertcode.cl"
        val r = "repartidor@insertcode.cl"
        val p1 = dao.insertarPedido(pedido(1, "Duoc UC Sede Alameda", "PROD-03", "Terminal POS Móvil Android", 2, 180000, EstadoPedido.CONFIRMADO, v))
        val p2 = dao.insertarPedido(pedido(2, "Empresa Tech Chile Ltd", "PROD-02", "Módulo IoT Rastreador GPS", 5, 120000, EstadoPedido.EN_DESPACHO, v))
        val p3 = dao.insertarPedido(pedido(3, "Comercial Central InsertCode", "PROD-01", "Licencia Dashboard Web Sales", 1, 450000, EstadoPedido.ENTREGADO, v))
        dao.insertarPedido(pedido(4, "Distribuidora Sur SpA", "PROD-04", "Soporte Premium Anual", 3, 90000, EstadoPedido.COTIZADO, v))
        val p5 = dao.insertarPedido(pedido(4, "Distribuidora Sur SpA", "PROD-02", "Módulo IoT Rastreador GPS", 2, 120000, EstadoPedido.CONFIRMADO, v))

        dao.insertarEntrega(EntregaEntity(0, p1, 1, "Duoc UC Sede Alameda", "Av. España 8, Santiago Centro", EstadoEntrega.PENDIENTE.name, r, 0.33f, 0.48f))
        dao.insertarEntrega(EntregaEntity(0, p2, 2, "Empresa Tech Chile Ltd", "Av. Providencia 1234, Of. 502", EstadoEntrega.EN_RUTA.name, r, 0.62f, 0.26f))
        dao.insertarEntrega(EntregaEntity(0, p3, 3, "Comercial Central InsertCode", "Av. Andrés Bello 2680, Las Condes", EstadoEntrega.ENTREGADO.name, r, 0.88f, 0.74f))
        // Sin asignar: cualquier distribuidor puede tomarla
        dao.insertarEntrega(EntregaEntity(0, p5, 4, "Distribuidora Sur SpA", "Gran Avenida 5120, San Miguel", EstadoEntrega.PENDIENTE.name, "", 0.55f, 0.82f))
    }

    private fun hoy(): String = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

    private fun pedido(
        clienteId: Int, cliente: String, codigo: String, producto: String,
        cantidad: Int, unitario: Int, estado: EstadoPedido, vendedor: String
    ) = PedidoEntity(
        clienteId = clienteId, clienteNombre = cliente, productoCodigo = codigo,
        productoNombre = producto, cantidad = cantidad, total = cantidad * unitario,
        estado = estado.name, vendedor = vendedor, fecha = hoy()
    )

    // ---------- Pedidos (Vendedor) ----------

    suspend fun crearPedido(cliente: ClienteEntity, producto: ProductoEntity, cantidad: Int, vendedor: String) {
        dao.insertarPedido(
            pedido(cliente.id, cliente.nombre, producto.codigo, producto.nombre, cantidad, producto.precio, EstadoPedido.COTIZADO, vendedor)
        )
    }

    // Al confirmar un pedido se genera su entrega pendiente (sin asignar)
    suspend fun cambiarEstadoPedido(pedido: PedidoEntity, nuevo: EstadoPedido, direccion: String) {
        dao.actualizarPedido(pedido.copy(estado = nuevo.name))
        if (nuevo == EstadoPedido.CONFIRMADO) {
            val orden = (dao.maxOrdenEntrega() ?: 0) + 1
            dao.insertarEntrega(
                EntregaEntity(
                    pedidoId = pedido.id, orden = orden, clienteNombre = pedido.clienteNombre,
                    direccion = direccion, estado = EstadoEntrega.PENDIENTE.name, repartidor = "",
                    px = 0.15f + (orden * 0.17f) % 0.75f, py = 0.25f + (orden * 0.23f) % 0.55f
                )
            )
        }
    }

    // ---------- Entregas (Distribuidor) ----------

    suspend fun actualizarEntrega(entrega: EntregaEntity, nuevo: EstadoEntrega, repartidor: String) {
        dao.actualizarEntrega(
            entrega.copy(estado = nuevo.name, repartidor = entrega.repartidor.ifBlank { repartidor })
        )
        // El estado de la entrega actualiza el del pedido
        val pedido = dao.pedidoPorId(entrega.pedidoId) ?: return
        val estadoPedido = when (nuevo) {
            EstadoEntrega.PENDIENTE -> EstadoPedido.CONFIRMADO
            EstadoEntrega.EN_RUTA -> EstadoPedido.EN_DESPACHO
            EstadoEntrega.ENTREGADO -> EstadoPedido.ENTREGADO
        }
        dao.actualizarPedido(pedido.copy(estado = estadoPedido.name))
    }
}
