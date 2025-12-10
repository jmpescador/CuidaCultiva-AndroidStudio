package com.example.cuidacultivo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_consulta")
data class HistorialConsulta(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val usuarioCedula: String,
    val plaga: String,
    val plagaDetectada: String,
    val metodo: String,
    val porcentaje: Double,
    val fecha: Long,

    // ✅ BASE64
    val imagenPath: String,

    // 0 = pendiente, 1 = enviado
    val enviado: Int
)
