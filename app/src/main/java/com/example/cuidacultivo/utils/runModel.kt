package com.example.cuidacultivo.tflite

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import com.example.cuidacultivo.data.plagasMap

fun runModel(context: Context, bitmap: Bitmap): String {

    // === 1. Cargar modelo e Interprete ===
    val modelBytes = context.assets.open("plagas_model.tflite").readBytes()
    val buffer = ByteBuffer.allocateDirect(modelBytes.size)
    buffer.order(ByteOrder.nativeOrder())
    buffer.put(modelBytes)

    val tflite = Interpreter(buffer)

    // === 2. Preparar Entrada ===
    val inputTensor = tflite.getInputTensor(0)
    val shape = inputTensor.shape()
    val inputH = shape[1]
    val inputW = shape[2]

    val resized = Bitmap.createScaledBitmap(bitmap, inputW, inputH, true)
    val inputBuffer = ByteBuffer.allocateDirect(4 * inputW * inputH * 3).order(ByteOrder.nativeOrder())

    for (y in 0 until inputH) {
        for (x in 0 until inputW) {
            val px = resized.getPixel(x, y)
            // Normalización: debe coincidir EXACTAMENTE con tu Python (x / 255.0)
            inputBuffer.putFloat((px shr 16 and 0xFF) / 255f)
            inputBuffer.putFloat((px shr 8 and 0xFF) / 255f)
            inputBuffer.putFloat((px and 0xFF) / 255f)
        }
    }

    // === 3. Ejecutar Inferencia ===
    // IMPORTANTE: Asegúrate de que el tamaño de output coincida con la cantidad de clases (16 en tu caso)
    val labels = listOf(
        "Acaros_mora", "Antracnosis", "Antracnosis_mora", "Aranita_roja",
        "Botrytis_mora", "Broca_del_cafe", "Chinche_chamusquina", "Cochinilla_verde",
        "Cochinillas_harinosas", "Escamas", "Mildeo_polvose_mora", "Minador_de_la_hoja",
        "Nematodos_del_cafe", "Phytophthora_mora", "Roya", "Trips_mora"
    )

    val output = Array(1) { FloatArray(labels.size) } // Ajustado dinámicamente al tamaño de tu lista
    tflite.run(inputBuffer, output)
    val probs = output[0]

    // === 4. Buscar la mejor predicción ===
    val maxIndex = probs.indices.maxByOrNull { probs[it] } ?: 0
    val bestLabel = labels[maxIndex]
    val bestProb = probs[maxIndex]

    Log.d("MODEL_DEBUG", "Predicción: $bestLabel con ${(bestProb * 100).toInt()}%")

    // ========================================================================
    // 🚨 CORRECCIÓN IMPORTANTE: UMBRAL DE CONFIANZA (THRESHOLD)
    // ========================================================================
    // En tu Python usaste 0.8. En móviles a veces se baja un poco a 0.6 o 0.7
    // Si la confianza es baja, devolvemos "No Detectado"

    val UMBRAL_CONFIANZA = 0.65f // 65% de seguridad mínima

    if (bestProb < UMBRAL_CONFIANZA) {
        return """
            {
              "id": "unknown",
              "nombre": "No se detectó plaga",
              "alias": "Planta sana o desconocida",
              "probabilidad": ${bestProb * 100},
              "descripcion": "El modelo no encontró coincidencias suficientes con las plagas entrenadas. Podría ser una hoja sana o una plaga no registrada.",
              "sintomas": "Asegúrate de enfocar bien la hoja afectada.",
              "control": "Intenta tomar la foto nuevamente con mejor luz."
            }
        """.trimIndent()
    }

    // ========================================================================

    // === 5. Obtener info desde plagasMap si pasó el umbral ===
    val info = plagasMap[bestLabel]

    // Fallback si la etiqueta existe en el modelo pero no escribiste info en el Map
    if (info == null) {
        return """
            {
              "id": "$bestLabel",
              "nombre": "$bestLabel",
              "alias": "",
              "probabilidad": ${bestProb * 100},
              "descripcion": "Información no disponible en la base de datos.",
              "sintomas": "Consultar con un agrónomo.",
              "control": "Sin recomendaciones."
            }
        """.trimIndent()
    }

    val json = """
        {
          "id": "$bestLabel",
          "nombre": "${info.nombre}",
          "alias": "${info.alias.joinToString(", ")}",
          "probabilidad": ${bestProb * 100},
          "descripcion": "${info.descripcion}",
          "sintomas": "${info.sintomas}",
          "control": "${info.control}"
        }
    """.trimIndent()

    return json
}