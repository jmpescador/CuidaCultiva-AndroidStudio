package com.example.cuidacultivo.data

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.example.cuidacultivo.data.remote.ApiClient

class UserRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val usuarioDao = db.usuarioDao()
    private val historialDao = db.historialDao()

    // ============================================================
    // ⭐ USUARIOS
    // ============================================================

    suspend fun guardarLocal(usuario: Usuario) {
        usuarioDao.insertar(usuario)
    }

    suspend fun obtenerUsuarioLocal(): Usuario? = usuarioDao.obtenerUsuario()

    suspend fun enviarAlBackend(usuario: Usuario): Usuario? {
        return try {
            val resp = ApiClient.service.crearUsuario(usuario)
            if (resp.isSuccessful) resp.body() else null
        } catch (e: Exception) {
            Log.e("REPO", "Error enviarAlBackend(): ${e.message}")
            null
        }
    }

    suspend fun actualizarRemoto(usuario: Usuario): Boolean {
        return try {
            val resp = ApiClient.service.actualizarUsuarioPorCedula(usuario.cedula, usuario)
            resp.isSuccessful
        } catch (e: Exception) {
            Log.e("REPO", "Error actualizarRemoto(): ${e.message}")
            false
        }
    }

    suspend fun actualizarLocal(usuario: Usuario) {
        usuarioDao.actualizar(usuario.copy(enviado = 0))
    }

    suspend fun marcarEnviado(usuario: Usuario) {
        usuarioDao.actualizar(usuario.copy(enviado = 1))
    }

    suspend fun sincronizarPendiente() {
        val user = usuarioDao.obtenerUsuario() ?: return

        val existe = ApiClient.service.obtenerPorCedula(user.cedula)

        if (existe.isSuccessful && existe.body() != null) {
            if (actualizarRemoto(user)) marcarEnviado(user)
        } else {
            if (enviarAlBackend(user) != null) marcarEnviado(user)
        }
    }

}
