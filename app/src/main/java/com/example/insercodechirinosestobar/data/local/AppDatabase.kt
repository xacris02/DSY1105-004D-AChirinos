package com.example.insercodechirinosestobar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        ProductoEntity::class, ClienteEntity::class, PedidoEntity::class,
        EntregaEntity::class, UsuarioEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun operacionDao(): OperacionDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        // Una sola instancia de la base de datos para toda la app
        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "insertcode.db"
                )
                    // Datos ficticios: si cambia el esquema se recrea la base
                    .fallbackToDestructiveMigration()
                    .build().also { instancia = it }
            }
    }
}
