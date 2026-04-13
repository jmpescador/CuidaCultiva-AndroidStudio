package com.example.cuidacultivo.data

import android.content.Context
import android.util.Log
import com.example.cuidacultivo.data.remote.ApiClient
import com.example.cuidacultivo.data.remote.LoginResponse

class UserRepository(context: Context) {

    private val api = ApiClient.getService(context)

    private val db = AppDatabase.getDatabase(context)
    private val usuarioDao = db.usuarioDao()
    private val historialDao = db.historialDao()

    // ============================================================
    // ⭐ USUARIOS
    // ============================================================

    suspend fun guardarLocal(usuario: Usuario) {
        usuarioDao.insertar(usuario)
    }

    suspend fun obtenerUsuarioLocal(): Usuario? =
        usuarioDao.obtenerUsuario()

    suspend fun enviarAlBackend(usuario: Usuario): Usuario? {
        return try {
            val resp = api.crearUsuario(usuario)
            if (resp.isSuccessful) resp.body() else null
        } catch (e: Exception) {
            Log.e("REPO", "Error enviarAlBackend(): ${e.message}")
            null
        }
    }

    suspend fun actualizarRemoto(usuario: Usuario): Boolean {
        return try {
            val resp = api.actualizarUsuarioPorCedula(usuario.cedula, usuario)
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

        try {
            val existe = api.obtenerPorCedula(user.cedula)

            if (existe.isSuccessful && existe.body() != null) {
                if (actualizarRemoto(user)) marcarEnviado(user)
            } else {
                if (enviarAlBackend(user) != null) marcarEnviado(user)
            }

        } catch (e: Exception) {
            Log.e("REPO", "Error sincronizarPendiente(): ${e.message}")
        }
    }

    // ============================================================
    // 🔐 PASSWORD / TOKEN
    // ============================================================

    suspend fun validarPassword(password: String): LoginResponse {
        return try {
            val resp = api.validarPassword(mapOf("password" to password))

            if (resp.isSuccessful && resp.body() != null) {
                resp.body()!!
            } else {
                LoginResponse(false, "")
            }

        } catch (e: Exception) {
            Log.e("REPO", "Error validarPassword(): ${e.message}")
            LoginResponse(false, "")
        }
    }

    suspend fun existeCedula(cedula: String): Boolean {
        return try {
            val resp = api.obtenerPorCedula(cedula)

            resp.isSuccessful && resp.body() != null

        } catch (e: Exception) {
            Log.e("REPO", "Error existeCedula(): ${e.message}")
            false
        }
    }
}