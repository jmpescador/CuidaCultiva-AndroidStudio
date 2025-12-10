package com.example.cuidacultivo.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.cuidacultivo.data.remote.ApiClient
import com.example.cuidacultivo.data.remote.HistorialRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import com.example.cuidacultivo.utils.toBase64


// -----------------------------------------------------
// Singleton para proveer la base de datos (Room)
// -----------------------------------------------------
object DatabaseProvider {
    lateinit var db: AppDatabase
}

// -----------------------------------------------------
// Repositorio de historial
// -----------------------------------------------------
class HistorialRepository(
    private val context: Context,
    private val dao: HistorialDao = DatabaseProvider.db.historialDao()
) {

    // -------------------------------
    // Guardar imagen como archivo
    // -------------------------------
    private fun guardarImagen(bitmap: Bitmap): String {
        val file = File(
            context.filesDir,
            "historial_${System.currentTimeMillis()}.jpg"
        )

        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, it)
        }

        return file.absolutePath
    }

    // -------------------------------------------------
    // Guardar historial local (Room)
    // -------------------------------------------------
    suspend fun guardarHistorialLocal(
        usuarioCedula: String,
        plagaDetectada: String,
        metodo: String,
        porcentaje: Double,
        bitmap: Bitmap
    ) = withContext(Dispatchers.IO) {

        Log.d("HIST_LOCAL", "➡️ Guardando historial local")
        Log.d("HIST_LOCAL", "Usuario: $usuarioCedula")
        Log.d("HIST_LOCAL", "Plaga: $plagaDetectada")

        val imagePath = guardarImagen(bitmap)

        val historial = HistorialConsulta(
            plaga = plagaDetectada,
            plagaDetectada = plagaDetectada,
            metodo = metodo,
            porcentaje = porcentaje,
            usuarioCedula = usuarioCedula,
            fecha = System.currentTimeMillis(),
            imagenPath = imagePath,
            enviado = 0
        )

        dao.insertar(historial)
        Log.d("HIST_LOCAL", "✅ Historial guardado en Room")
    }

    // -------------------------------------------------
    // Sincronizar historial pendiente con backend
    // -------------------------------------------------
    suspend fun sincronizarHistorial() = withContext(Dispatchers.IO) {

        try {
            Log.d("HIST_SYNC", "🔄 Iniciando sincronización")

            val pendientes = dao.getPendientes()
            Log.d("HIST_SYNC", "Pendientes: ${pendientes.size}")

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

            for (item in pendientes) {
                Log.d("HIST_SYNC", "➡️ Enviando ID: ${item.id}")

                // Convertir imagen a Base64 SOLO aquí
                val bitmap = BitmapFactory.decodeFile(item.imagenPath)
                val imagenBase64 = bitmap.toBase64()

                val request = HistorialRequest(
                    usuario_cedula = item.usuarioCedula,
                    plaga_detectada = item.plagaDetectada,
                    metodo = item.metodo,
                    porcentaje = item.porcentaje,
                    fecha = sdf.format(Date(item.fecha)),
                    imagen = imagenBase64
                )

                val response = withTimeout(10_000) {
                    ApiClient.service.enviarHistorial(request)
                }

                Log.d("HIST_SYNC", "✅ HTTP ${response.code()}")

                if (response.isSuccessful) {
                    dao.actualizar(item.copy(enviado = 1))
                    Log.d("HIST_SYNC", "✅ Marcado como enviado")
                }
            }

        } catch (e: TimeoutCancellationException) {
            Log.e("HIST_SYNC", "⏱️ Timeout: backend no responde")
        } catch (e: Exception) {
            Log.e("HIST_SYNC", "🔥 Error sincronizando historial", e)
        }
    }

    // -------------------------------------------------
    // Obtener historial local
    // -------------------------------------------------
    suspend fun obtenerHistorial(): List<HistorialConsulta> =
        withContext(Dispatchers.IO) {
            dao.getAll()
        }
}

// -----------------------------------------------------
// Helper único para guardar historial desde UI
// -----------------------------------------------------
fun saveHistorial(
    context: Context,
    plaga: String,
    metodo: String,
    porcentaje: Double,
    bitmap: Bitmap
) {
    val historialRepo = HistorialRepository(context)
    val userRepo = UserRepository(context)

    CoroutineScope(Dispatchers.IO).launch {
        val usuario = userRepo.obtenerUsuarioLocal() ?: return@launch

        historialRepo.guardarHistorialLocal(
            usuarioCedula = usuario.cedula,
            plagaDetectada = plaga,
            metodo = metodo,
            porcentaje = porcentaje,
            bitmap = bitmap
        )

        historialRepo.sincronizarHistorial()
    }
}
