package com.example.cuidacultivo.data

import android.R
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val telefono: String,
    val cedula: String,
    val enviado: Int = 0, // 0 = pendiente, 1 = enviado
    val foto: String? = null,
    val direccion: String? = null
) : Serializable
