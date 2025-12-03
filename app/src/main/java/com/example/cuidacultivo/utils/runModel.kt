package com.example.cuidacultivo.tflite

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class Prediccion(val label: String, val alias: String, val prob: Float)

fun runModel(context: Context, bitmap: Bitmap): String {

    val modelBytes = context.assets.open("plagas_model.tflite").readBytes()
    val buffer = ByteBuffer.allocateDirect(modelBytes.size)
    buffer.order(ByteOrder.nativeOrder())
    buffer.put(modelBytes)

    val tflite = Interpreter(buffer)

    // INPUT SHAPE
    val inputTensor = tflite.getInputTensor(0)
    val shape = inputTensor.shape() // ej: [1,128,128,3]
    val inputH = shape[1]
    val inputW = shape[2]

    val resized = Bitmap.createScaledBitmap(bitmap, inputW, inputH, true)
    val inputBuffer = ByteBuffer.allocateDirect(4 * inputW * inputH * 3).order(ByteOrder.nativeOrder())

    for (y in 0 until inputH) {
        for (x in 0 until inputW) {
            val px = resized.getPixel(x, y)
            inputBuffer.putFloat((px shr 16 and 0xFF) / 255f)
            inputBuffer.putFloat((px shr 8 and 0xFF) / 255f)
            inputBuffer.putFloat((px and 0xFF) / 255f)
        }
    }

    // OUTPUT 16 clases
    val output = Array(1) { FloatArray(16) }
    tflite.run(inputBuffer, output)
    val probs = output[0]

    // LABELS y ALIAS
    val labels = listOf(
        "Acaros_mora","Antracnosis","Antracnosis_mora","Aranita_roja",
        "Botrytis_mora","Broca_del_cafe","Chinche_chamusquina","Cochinilla_verde",
        "Cochinillas_harinosas","Escamas","Mildeo_polvose_mora","Minador_de_la_hoja",
        "Nematodos_del_cafe","Phytophthora_mora","Roya","Trips_mora"
    )

    val aliases = listOf(
        "acaros","antracnosis","antracnosis","aranita",
        "botrytis","broca, broca del café, broca del cafetal","chinche, chinche chamusquina, chinche de la hoja","cochinilla verde, cochinilla",
        "cochinilla harinosa, cochinillas","escamas","mildeo","minador de la hoja",
        "nematodos","phytophthora","roya","trips"
    )

    // TOP 3 predicciones
    val top3 = probs
        .mapIndexed { idx, p -> Prediccion(labels[idx], aliases[idx], p) }
        .sortedByDescending { it.prob }
        .take(3)

    // Crear string final
    val sb = StringBuilder()
    sb.append("Top 3 predicciones:\n")
    top3.forEach { sb.append("${it.label} (${String.format("%.2f", it.prob*100)}%) - Alias: ${it.alias}\n") }

    // Predicción dudosa si max < 0.8
    val maxProb = top3.firstOrNull()?.prob ?: 0f
    if (maxProb < 0.8f) {
        sb.append("\n⚠️ Predicción dudosa (confianza ${String.format("%.2f", maxProb*100)}%)")
    }

    val resultStr = sb.toString()
    Log.e("MODEL_TOP3", resultStr)

    return resultStr
}
