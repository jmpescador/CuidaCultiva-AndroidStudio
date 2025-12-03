package com.example.cuidacultivo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// 🔹 Base de datos principal
@Database(
    entities = [Usuario::class],
    version = 4, // ↑ Aumenta la versión cada vez que cambies la entidad
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

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
