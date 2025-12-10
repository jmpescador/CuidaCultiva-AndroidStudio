package com.example.cuidacultivo.data.remote

data class HistorialRequest(
    val usuario_cedula: String,
    val plaga_detectada: String,
    val metodo: String,
    val porcentaje: Double,
    val fecha: String,
    val imagen: String? = null
)

