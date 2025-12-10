package com.example.cuidacultivo.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface HistorialDao {

    @Insert
    suspend fun insertar(historial: HistorialConsulta)

    @Query("SELECT * FROM historial_consulta ORDER BY fecha DESC")
    suspend fun getAll(): List<HistorialConsulta>

    @Query("SELECT * FROM historial_consulta WHERE enviado = 0")
    suspend fun getPendientes(): List<HistorialConsulta>

    @Update
    suspend fun actualizar(historial: HistorialConsulta)
}
