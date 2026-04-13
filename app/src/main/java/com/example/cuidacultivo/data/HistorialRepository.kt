package com.example.cuidacultivo.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.cuidacultivo.data.remote.ApiClient
import com.example.cuidacultivo.data.remote.HistorialRequest
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import com.example.cuidacultivo.utils.toBase64

// -----------------------------------------------------
// Singleton DB
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

    // 🔥 API con interceptor (TOKEN)
    private val api = ApiClient.getService(context)

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
    // Guardar historial local
    // -------------------------------------------------
    suspend fun guardarHistorialLocal(
        usuarioCedula: String,
        plagaDetectada: String,
        metodo: String,
        porcentaje: Double,
        bitmap: Bitmap
    ) = withContext(Dispatchers.IO) {

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
        Log.d("HIST_LOCAL", "✅ Guardado local")
    }

    // -------------------------------------------------
    // 🔥 SINCRONIZAR CON BACKEND
    // -------------------------------------------------
    suspend fun sincronizarHistorial() = withContext(Dispatchers.IO) {

        try {
            val pendientes = dao.getPendientes()
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

            for (item in pendientes) {

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
                    api.enviarHistorial(request) // 🔥 CAMBIO CLAVE
                }

                Log.d("HIST_SYNC", "HTTP ${response.code()}")

                if (response.isSuccessful) {
                    dao.actualizar(item.copy(enviado = 1))
                    Log.d("HIST_SYNC", "✅ Enviado")
                }
            }

        } catch (e: TimeoutCancellationException) {
            Log.e("HIST_SYNC", "⏱ Timeout")
        } catch (e: Exception) {
            Log.e("HIST_SYNC", "🔥 Error", e)
        }
    }

    // -------------------------------------------------
    // Obtener historial
    // -------------------------------------------------
    suspend fun obtenerHistorial(): List<HistorialConsulta> =
        withContext(Dispatchers.IO) {
            dao.getAll()
        }
}

// -----------------------------------------------------
// Helper desde UI
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