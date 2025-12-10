package com.example.cuidacultivo.utils

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

fun Bitmap.toBase64(): String {
    val baos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 75, baos) // 🔥 75% recomendado
    val bytes = baos.toByteArray()
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}
