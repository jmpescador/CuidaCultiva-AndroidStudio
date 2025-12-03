package com.example.tuapp.utils

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream

fun loadModelFile(context: Context, modelName: String): MappedByteBuffer {
    val fileDescriptor = context.assets.openFd(modelName)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = fileDescriptor.startOffset
    val declaredLength = fileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}

fun runModel(context: Context, bitmap: Bitmap): String {
    val tflite = Interpreter(loadModelFile(context, "plagas_model.tflite"))

    // Ajusta tamaño según tu modelo
    val inputBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
    val input = Array(1) { Array(224) { Array(224) { FloatArray(3) } } }

    for (x in 0 until 224) {
        for (y in 0 until 224) {
            val pixel = inputBitmap.getPixel(x, y)
            input[0][x][y][0] = ((pixel shr 16 and 0xFF) / 255f)
            input[0][x][y][1] = ((pixel shr 8 and 0xFF) / 255f)
            input[0][x][y][2] = ((pixel and 0xFF) / 255f)
        }
    }

    val output = Array(1) { FloatArray(5) } // Cambia 5 por el número de clases de tu modelo
    tflite.run(input, output)

    val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
    val labels = listOf("Plaga1", "Plaga2", "Plaga3", "Plaga4", "Plaga5") // Tus etiquetas
    return if (maxIndex >= 0) labels[maxIndex] else "Desconocida"
}
