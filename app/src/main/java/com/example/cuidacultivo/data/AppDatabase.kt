package com.example.cuidacultivo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 🔹 Base de datos principal
@Database(
    entities = [
        Usuario::class,
        HistorialConsulta::class   // ← 🔥 AGREGA ESTA TABLA
    ],
    version = 2, // ← AUMENTA LA VERSIÓN
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun historialDao(): HistorialDao  // ← 🔥 AGREGA ESTE DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cuida_cultivo_db"
                )
                    .fallbackToDestructiveMigration() // ← elimina DB vieja y crea nueva
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
