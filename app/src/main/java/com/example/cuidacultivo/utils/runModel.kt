package com.example.cuidacultivo.tflite

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

// Importa tu mapa
import com.example.cuidacultivo.data.plagasMap

fun runModel(context: Context, bitmap: Bitmap): String {

    // === Cargar modelo ===
    val modelBytes = context.assets.open("plagas_model.tflite").readBytes()
    val buffer = ByteBuffer.allocateDirect(modelBytes.size)
    buffer.order(ByteOrder.nativeOrder())
    buffer.put(modelBytes)

    val tflite = Interpreter(buffer)

    // === Obtener tamaño de entrada ===
    val inputTensor = tflite.getInputTensor(0)
    val shape = inputTensor.shape() // [1,128,128,3]
    val inputH = shape[1]
    val inputW = shape[2]

    // === Preparar imagen ===
    val resized = Bitmap.createScaledBitmap(bitmap, inputW, inputH, true)
    val inputBuffer =
        ByteBuffer.allocateDirect(4 * inputW * inputH * 3).order(ByteOrder.nativeOrder())

    for (y in 0 until inputH) {
        for (x in 0 until inputW) {
            val px = resized.getPixel(x, y)
            inputBuffer.putFloat((px shr 16 and 0xFF) / 255f)
            inputBuffer.putFloat((px shr 8 and 0xFF) / 255f)
            inputBuffer.putFloat((px and 0xFF) / 255f)
        }
    }

    // === Salida: 16 clases ===
    val output = Array(1) { FloatArray(2) }
    tflite.run(inputBuffer, output)
    val probs = output[0]

    // === Labels ===
    val labels = listOf(
        "Acaros_mora", "Antracnosis", "Antracnosis_mora", "Aranita_roja",
        "Botrytis_mora", "Broca_del_cafe", "Chinche_chamusquina", "Cochinilla_verde",
        "Cochinillas_harinosas", "Escamas", "Mildeo_polvose_mora", "Minador_de_la_hoja",
        "Nematodos_del_cafe", "Phytophthora_mora", "Roya", "Trips_mora"
    )

    // === Mejor predicción ===
    val maxIndex = probs.indices.maxByOrNull { probs[it] } ?: 0
    val bestLabel = labels[maxIndex]
    val bestProb = probs[maxIndex]

    // === Obtener info desde plagasMap ===
    val info = plagasMap[bestLabel]

    if (info == null) {
        return """
            {
              "id": "$bestLabel",
              "nombre": "$bestLabel",
              "alias": "",
              "probabilidad": ${bestProb * 100},
              "descripcion": "Sin descripción.",
              "sintomas": "Sin síntomas.",
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

    Log.e("JSON_FINAL", json)

    return json
}

fun runModelText(context: Context, texto: String): String {
    val lowerText = texto.lowercase()

    // Buscar coincidencia en plagasMap por nombre o alias
    val match = plagasMap.entries.find { (_, info) ->
        info.nombre.lowercase().contains(lowerText) ||
                info.alias.any { alias -> alias.lowercase().contains(lowerText) }
    }

    val json = if (match != null) {
        val info = match.value
        """
            {
              "id": "${match.key}",
              "nombre": "${info.nombre}",
              "alias": "${info.alias.joinToString(", ")}",
              "probabilidad": 100,
              "descripcion": "${info.descripcion}",
              "sintomas": "${info.sintomas}",
              "control": "${info.control}"
            }
        """.trimIndent()
    } else {
        """
            {
              "id": "no_encontrado",
              "nombre": "No encontrado",
              "alias": "",
              "probabilidad": 0,
              "descripcion": "No se encontró ninguna plaga que coincida con la descripción.",
              "sintomas": "",
              "control": ""
            }
        """.trimIndent()
    }

    Log.e("JSON_TEXT", json)
    return json
}