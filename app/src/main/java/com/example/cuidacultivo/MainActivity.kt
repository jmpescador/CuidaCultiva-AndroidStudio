package com.example.cuidacultivo

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.cuidacultivo.data.UserRepository
import com.example.cuidacultivo.data.Usuario
import com.example.cuidacultivo.ui.navigation.AppNavigation
import com.example.cuidacultivo.ui.screens.RegistroUsuarioScreen
import com.example.cuidacultivo.ui.theme.CuidaCultivoTheme
import com.example.cuidacultivo.ui.screens.PasswordScreen
import com.example.cuidacultivo.utils.uriToBase64
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            val context = this
            val repo = remember { UserRepository(context) }
            val scope = rememberCoroutineScope()

            var usuarioRegistrado by remember { mutableStateOf<Boolean?>(null) }

            var accesoValidado by remember { mutableStateOf<Boolean?>(null) }

            // ============================================================
            // ⭐ CARGA INICIAL → LEER ROOM y SINCRONIZAR CON BACKEND
            // ============================================================
            LaunchedEffect(Unit) {
                val user = withContext(Dispatchers.IO) { repo.obtenerUsuarioLocal() }
                usuarioRegistrado = user != null

                val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)
                val acceso = prefs.getBoolean("acceso_validado", false)
                accesoValidado = acceso

                if (user != null && tieneInternet(context)) {
                    try {
                        Log.d("SYNC", "🔄 Ejecutando sincronización inicial...")
                        repo.sincronizarPendiente()
                    } catch (e: Exception) {
                        Log.e("SYNC", "❌ Error al sincronizar: ${e.message}")
                    }
                }
            }

            CuidaCultivoTheme {

                when {

                    // ⏳ LOADING
                    usuarioRegistrado == null || accesoValidado == null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    // 👤 NO REGISTRADO → REGISTRO
                    usuarioRegistrado == false -> {
                        RegistroUsuarioScreen { nombre, telefono, cedula, direccion, fotoUri ->

                            scope.launch(Dispatchers.IO) {

                                // 🔍 VALIDAR CÉDULA PRIMERO
                                val existe = if (tieneInternet(context)) {
                                    repo.existeCedula(cedula)
                                } else {
                                    false // si no hay internet, dejamos registrar
                                }

                                if (existe) {
                                    withContext(Dispatchers.Main) {
                                        android.widget.Toast
                                            .makeText(context, "La cédula ya está registrada", android.widget.Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                    return@launch
                                }

                                // 📸 convertir imagen
                                val base64Foto = uriToBase64(context, fotoUri)

                                val usuario = Usuario(
                                    id = 0,
                                    nombre = nombre,
                                    telefono = telefono,
                                    cedula = cedula,
                                    direccion = direccion,
                                    foto = base64Foto,
                                    enviado = 0
                                )

                                // 💾 guardar local
                                repo.guardarLocal(usuario)

                                withContext(Dispatchers.Main) {
                                    usuarioRegistrado = true
                                }

                                // 🔄 sincronizar si hay internet
                                if (tieneInternet(context)) {
                                    repo.sincronizarPendiente()
                                }
                            }
                        }
                    }

                    // 🔐 REGISTRADO PERO SIN PASSWORD
                    accesoValidado == false -> {
                        PasswordScreen { passwordIngresada ->

                            try {
                                val response = repo.validarPassword(passwordIngresada)

                                if (response.exito) {

                                    val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)
                                    prefs.edit()
                                        .putBoolean("acceso_validado", true)
                                        .putString("token", response.token)
                                        .apply()

                                    accesoValidado = true

                                    true // ✅ IMPORTANTE: retorna true

                                } else {
                                    Log.e("LOGIN", "❌ Password incorrecta")
                                    false // ❌ retorna false
                                }

                            } catch (e: Exception) {
                                Log.e("LOGIN", "❌ Error: ${e.message}")
                                false // ❌ error también retorna false
                            }
                        }
                    }

                    // ✅ TODO OK → HOME
                    else -> {
                        val navController = rememberNavController()
                        AppNavigation(navController)
                    }
                }
            }
        }
    }

    // ============================================================
    // ⭐ VERIFICAR INTERNET
    // ============================================================
    private fun tieneInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
