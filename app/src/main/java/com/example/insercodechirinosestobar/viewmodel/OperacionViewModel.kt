package com.example.insercodechirinosestobar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.insercodechirinosestobar.data.local.ClienteEntity
import com.example.insercodechirinosestobar.data.local.EntregaEntity
import com.example.insercodechirinosestobar.data.local.PedidoEntity
import com.example.insercodechirinosestobar.data.local.ProductoEntity
import com.example.insercodechirinosestobar.data.local.UsuarioEntity
import com.example.insercodechirinosestobar.model.EstadoEntrega
import com.example.insercodechirinosestobar.model.EstadoPedido
import com.example.insercodechirinosestobar.model.OperacionDatos
import com.example.insercodechirinosestobar.model.Rol
import com.example.insercodechirinosestobar.repository.OperacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OperacionViewModel(app: Application) : AndroidViewModel(app) {

    private val repositorio = OperacionRepository(app.applicationContext)

    private val _datos = MutableStateFlow(OperacionDatos())
    val datos: StateFlow<OperacionDatos> = _datos.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.sembrar()
            repositorio.datos.collect { _datos.value = it }
        }
    }

    // Vendedor: cotiza/registra un pedido
    fun crearPedido(cliente: ClienteEntity, producto: ProductoEntity, cantidad: Int, vendedor: String) {
        if (cantidad < 1) return
        viewModelScope.launch { repositorio.crearPedido(cliente, producto, cantidad, vendedor) }
    }

    // Vendedor: confirma o cancela un pedido
    fun cambiarEstadoPedido(pedido: PedidoEntity, nuevo: EstadoPedido) {
        val direccion = _datos.value.clientes.find { it.id == pedido.clienteId }?.direccion ?: "Dirección por confirmar"
        viewModelScope.launch { repositorio.cambiarEstadoPedido(pedido, nuevo, direccion) }
    }

    // Distribuidor: actualiza el estado de una entrega
    fun actualizarEntrega(entrega: EntregaEntity, nuevo: EstadoEntrega, repartidor: String) {
        viewModelScope.launch { repositorio.actualizarEntrega(entrega, nuevo, repartidor) }
    }

    // Administrador: usuarios de prueba, roles y permisos
    fun cambiarRol(usuario: UsuarioEntity, rol: Rol) {
        viewModelScope.launch { repositorio.guardarUsuario(usuario.copy(rol = rol.name)) }
    }

    fun alternarActivo(usuario: UsuarioEntity) {
        viewModelScope.launch { repositorio.guardarUsuario(usuario.copy(activo = !usuario.activo)) }
    }

    fun crearUsuarioPrueba(email: String, nombre: String, rol: Rol) {
        if (email.isBlank()) return
        viewModelScope.launch {
            repositorio.guardarUsuario(
                UsuarioEntity(email.trim(), nombre.ifBlank { email.substringBefore("@") }, rol.name, true)
            )
        }
    }
}
