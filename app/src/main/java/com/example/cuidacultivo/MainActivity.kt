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

            // ============================================================
            // ⭐ CARGA INICIAL → LEER ROOM y SINCRONIZAR CON BACKEND
            // ============================================================
            LaunchedEffect(Unit) {
                val user = withContext(Dispatchers.IO) { repo.obtenerUsuarioLocal() }
                usuarioRegistrado = user != null

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

                when (usuarioRegistrado) {

                    // -----------------------------------------------------------
                    // ⭐ MOSTRAR LOADING MIENTRAS CARGA ROOM
                    // -----------------------------------------------------------
                    null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    // -----------------------------------------------------------
                    // ⭐ NO HAY USUARIO → MOSTRAR REGISTRO
                    // -----------------------------------------------------------
                    false -> {
                        RegistroUsuarioScreen { nombre, telefono, cedula, direccion, fotoUri ->

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

                            Log.d("REGISTRO", "📝 Iniciando registro de usuario: $usuario")

                            scope.launch(Dispatchers.IO) {
                                try {
                                    Log.d("REGISTRO", "💾 Guardando usuario localmente...")
                                    repo.guardarLocal(usuario)
                                    Log.d("REGISTRO", "✔ Usuario guardado en Room")
                                } catch (e: Exception) {
                                    Log.e("REGISTRO", "❌ Error guardando en Room: ${e.message}")
                                }

                                withContext(Dispatchers.Main) {
                                    Log.d("REGISTRO", "➡ Pasando al home")
                                    usuarioRegistrado = true  // Ir al home inmediatamente
                                }

                                if (tieneInternet(context)) {
                                    try {
                                        Log.d("REGISTRO", "🌐 Hay internet → sincronizando con backend...")
                                        repo.sincronizarPendiente()
                                        Log.d("REGISTRO", "✔ Sincronización con backend finalizada")
                                    } catch (e: Exception) {
                                        Log.e("REGISTRO", "❌ Error sincronizando con backend: ${e.message}")
                                    }
                                } else {
                                    Log.d("REGISTRO", "⚠ No hay internet → sincronización pendiente")
                                }
                            }
                        }


                    }

                    // -----------------------------------------------------------
                    // ⭐ USUARIO YA EXISTE → IR A LA APP
                    // -----------------------------------------------------------
                    true -> {
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
