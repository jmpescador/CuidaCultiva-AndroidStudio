package com.example.cuidacultivo.data.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 🔹 Extensión para inicializar DataStore en el contexto
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    // 🔹 Llave para verificar si el usuario ya se registró
    private val REGISTRADO_KEY = booleanPreferencesKey("registrado")

    // 🔹 Flow que expone el estado actual
    val registrado: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[REGISTRADO_KEY] ?: false
    }

    // 🔹 Guardar el valor (true / false)
    suspend fun setRegistrado(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[REGISTRADO_KEY] = value
        }
    }
}
