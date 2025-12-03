package com.example.cuidacultivo.data

import androidx.room.*

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: Usuario)

    @Update
    suspend fun actualizar(usuario: Usuario): Int

    @Query("SELECT * FROM usuarios")
    suspend fun obtenerTodos(): List<Usuario>

    @Query("SELECT * FROM usuarios WHERE enviado = 0")
    suspend fun obtenerNoEnviados(): List<Usuario>

    @Query("SELECT * FROM usuarios LIMIT 1")
    suspend fun obtenerUsuario(): Usuario?

    @Query("DELETE FROM usuarios")
    suspend fun borrarTodo()
}
