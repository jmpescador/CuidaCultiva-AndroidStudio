package com.example.cuidacultivo.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cuidacultivo.data.HistorialRepository
import com.example.cuidacultivo.data.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistorialViewModel(
    private val context: Context
) : ViewModel() {

    private val userRepo = UserRepository(context)

    /**
     * Guarda historial local y lo sincroniza con backend
     */
    fun guardarYEnviar(
        metodo: String,
        plaga: String,
        porcentaje: Double,
        bitmap: Bitmap
    ) {
        viewModelScope.launch(Dispatchers.IO) {


        }
    }
}
