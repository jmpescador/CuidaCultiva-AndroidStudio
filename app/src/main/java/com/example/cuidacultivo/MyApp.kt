package com.example.cuidacultivo

import android.app.Application
import androidx.room.Room
import com.example.cuidacultivo.data.AppDatabase
import com.example.cuidacultivo.data.DatabaseProvider

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicializar la base de datos
        DatabaseProvider.db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "cuida_cultivo_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
