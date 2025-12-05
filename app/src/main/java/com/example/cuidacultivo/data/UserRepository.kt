package com.example.cuidacultivo.data

import android.content.Context
import android.util.Log
import com.example.cuidacultivo.data.remote.ApiClient

class UserRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val dao = db.usuarioDao()

    // ============================================================
    // ⭐ GUARDAR LOCAL
    // ============================================================
    suspend fun guardarLocal(usuario: Usuario) {
        Log.d("REPO", "📝 guardarLocal() → Guardando usuario local: $usuario")
        dao.insertar(usuario)
        Log.d("REPO", "✔ Usuario guardado en Room")
    }

    // ============================================================
    // ⭐ OBTENER LOCAL
    // ============================================================
    suspend fun obtenerUsuarioLocal(): Usuario? {
        Log.d("REPO", "🔍 obtenerUsuarioLocal() → Buscando usuario en Room")
        val user = dao.obtenerUsuario()
        Log.d("REPO", "📌 Resultado obtenerUsuarioLocal(): $user")
        return user
    }

    // ============================================================
    // ⭐ ENVIAR AL BACKEND (CREAR)
    // ============================================================
    suspend fun enviarAlBackend(usuario: Usuario): Usuario? {
        Log.d("REPO", " enviarAlBackend() → Enviando usuario al backend: $usuario")

        return try {
            val response = ApiClient.service.crearUsuario(usuario)
            Log.d(
                "REPO",
                "🌍 Respuesta crearUsuario: code=${response.code()}, body=${response.body()}"
            )

            if (response.isSuccessful) {
                Log.d("REPO", "✔ Usuario creado en backend correctamente")
                response.body()
            } else {
                Log.e("REPO", "❌ Error backend al crear usuario: ${response.errorBody()?.string()}")
                null
            }

        } catch (e: Exception) {
            Log.e("REPO", "❌ EXCEPCIÓN enviarAlBackend(): ${e.message}")
            null
        }
    }

    // ============================================================
    // ⭐ ACTUALIZAR REMOTO (PUT)
    // ============================================================
    suspend fun actualizarRemoto(usuario: Usuario): Boolean {
        Log.d("REPO", "🌐 actualizarRemoto() → Actualizando usuario por cédula ${usuario.cedula}")

        return try {
            val response = ApiClient.service.actualizarUsuarioPorCedula(usuario.cedula, usuario)
            Log.d("REPO", " Response code: ${response.code()}")
            Log.d("REPO", " Response body: ${response.body()}")
            Log.d("REPO", " Response error: ${response.errorBody()?.string() ?: "No hay error"}")

            if (response.isSuccessful) {
                Log.d("REPO", "✔ Usuario actualizado exitosamente en backend")
                true
            } else {
                Log.e("REPO", "❌ Error backend al actualizar usuario: ${response.errorBody()?.string()}")
                false
            }

        } catch (e: Exception) {
            Log.e("REPO", "❌ EXCEPCIÓN actualizarRemoto(): ${e.message}")
            false
        }
    }

    // ============================================================
    // ⭐ ACTUALIZAR LOCAL (CAMBIAR ESTADO)
    // ============================================================
    suspend fun actualizarLocal(usuario: Usuario) {
        Log.d("REPO", "📝 actualizarLocal() → Marcando usuario como enviado=0")
        dao.actualizar(usuario.copy(enviado = 0))
        Log.d("REPO", "✔ Usuario marcado como pendiente de envío")
    }

    // ============================================================
    // ⭐ MARCAR COMO ENVIADO
    // ============================================================
    suspend fun marcarEnviado(usuario: Usuario) {
        Log.d("REPO", "🏁 marcarEnviado() → Cambiando enviado=1")
        dao.actualizar(usuario.copy(enviado = 1))
        Log.d("REPO", "✔ Usuario marcado como enviado en Room")
    }

    // ============================================================
    // ⭐ SINCRONIZACIÓN PRINCIPAL
    // ============================================================
    suspend fun sincronizarPendiente() {
        Log.d("REPO", "🔄 sincronizarPendiente() → Iniciando sincronización")

        val user = dao.obtenerUsuario()
        Log.d("REPO", "📌 Usuario en Room: $user")

        if (user == null) {
            Log.d("REPO", "⚠ No hay usuario que sincronizar")
            return
        }

        // 1️⃣ CONSULTAR POR CÉDULA EN BACKEND
        Log.d("REPO", "🔍 Buscando usuario por cédula ${user.cedula} en backend...")

        val response = ApiClient.service.obtenerPorCedula(user.cedula)

        val existeEnBackend = response.isSuccessful && response.body() != null

        if (existeEnBackend) {
            // ====================================================
            // ⭐ SI EXISTE → ACTUALIZAR (PUT)
            // ====================================================
            Log.d("REPO", "🟢 Usuario existe → Actualizando en backend...")

            val actualizado = actualizarRemoto(user)

            if (actualizado) {
                Log.d("REPO", "✔ Usuario ACTUALIZADO → Marcando enviado=1")
                marcarEnviado(user)
            } else {
                Log.e("REPO", "❌ Falló actualización → enviado=1 pero desactualizado")
            }

        } else {
            // ====================================================
            // ⭐ SI NO EXISTE → CREAR (POST)
            // ====================================================
            Log.d("REPO", "🟡 Usuario NO existe → Creando en backend...")

            val creado = enviarAlBackend(user)

            if (creado != null) {
                Log.d("REPO", "✔ Usuario CREADO → Marcando enviado=1")
                marcarEnviado(user)
            } else {
                Log.e("REPO", "❌ Falló creación → seguirá enviado=0")
            }
        }
    }

}
