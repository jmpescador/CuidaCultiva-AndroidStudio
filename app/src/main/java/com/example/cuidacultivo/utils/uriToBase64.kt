package com.example.cuidacultivo.utils

import android.content.Context
import android.net.Uri
import android.util.Base64

fun uriToBase64(context: Context, uri: Uri?): String? {
    return try {
        if (uri == null) return null
        val stream = context.contentResolver.openInputStream(uri) ?: return null
        val bytes = stream.readBytes()
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
