package com.example.insercodechirinosestobar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OperacionDao {

    @Query("SELECT * FROM productos ORDER BY codigo")
    fun observarProductos(): Flow<List<ProductoEntity>>

    @Query("SELECT * FROM clientes ORDER BY nombre")
    fun observarClientes(): Flow<List<ClienteEntity>>

    @Query("SELECT * FROM pedidos ORDER BY id DESC")
    fun observarPedidos(): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM entregas ORDER BY orden")
    fun observarEntregas(): Flow<List<EntregaEntity>>

    @Query("SELECT * FROM usuarios ORDER BY email")
    fun observarUsuarios(): Flow<List<UsuarioEntity>>

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun contarProductos(): Int

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun usuarioPorEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM pedidos WHERE id = :id LIMIT 1")
    suspend fun pedidoPorId(id: Long): PedidoEntity?

    @Query("SELECT MAX(orden) FROM entregas")
    suspend fun maxOrdenEntrega(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProductos(productos: List<ProductoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarClientes(clientes: List<ClienteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPedido(pedido: PedidoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEntrega(entrega: EntregaEntity): Long

    // IGNORE: nunca pisa el rol que el administrador ya cambió
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarUsuarioSiNoExiste(usuario: UsuarioEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarUsuario(usuario: UsuarioEntity)

    @Update
    suspend fun actualizarPedido(pedido: PedidoEntity)

    @Update
    suspend fun actualizarEntrega(entrega: EntregaEntity)
}
