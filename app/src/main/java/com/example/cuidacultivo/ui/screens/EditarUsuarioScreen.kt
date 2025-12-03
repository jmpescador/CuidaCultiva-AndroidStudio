package com.example.cuidacultivo.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.cuidacultivo.R
import com.example.cuidacultivo.data.Usuario
import com.example.cuidacultivo.ui.components.menu.LayoutMenu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cuidacultivo.data.UserRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.ui.text.style.TextOverflow

fun tieneInternet(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nw = cm.activeNetwork ?: return false
    val actNw = cm.getNetworkCapabilities(nw) ?: return false
    return actNw.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}



@Composable
fun EditarUsuarioScreen(
    navController: NavController,
    usuario: Usuario,
    onGuardarCambios: (Usuario) -> Unit
) {

    var nombre by remember { mutableStateOf(usuario.nombre) }
    var telefono by remember { mutableStateOf(usuario.telefono ?: "") }
    var direccion by remember { mutableStateOf(usuario.direccion ?: "") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val userRepository = UserRepository(context)


    val scroll = rememberScrollState()

    // === Selección de imagen ===
    val pickImageLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) pickImageLauncher.launch("image/*")
    }

    // === Botón gradiente ===
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF1976D2), Color(0xFF002E4A))
    )

    LayoutMenu(navController = navController) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scroll)
        ) {

            // ==========================================================
            // FOTO + NOMBRE
            // ==========================================================
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.2f))
                        .clickable {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.READ_MEDIA_IMAGES
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                pickImageLauncher.launch("image/*")
                            } else {
                                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {

                    when {
                        imageUri != null -> Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        usuario.foto != null -> {
                            val bitmap = remember(usuario.foto) {
                                try {
                                    val bytes = Base64.decode(usuario.foto, Base64.DEFAULT)
                                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                } catch (e: Exception) { null }
                            }

                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        else -> Text("Seleccionar foto", fontSize = 14.sp, textAlign = TextAlign.Center)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = nombre,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,                 // Solo una línea
                    overflow = TextOverflow.Ellipsis, // Mostrar "..." si es muy largo
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Agricultor", fontSize = 18.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(50.dp))


            // ==========================================================
            // INFORMACIÓN PERSONAL
            // ==========================================================
            Text(
                "Información personal",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            EditarCampoEditable(
                label = "Nombre",
                value = nombre,
                icon = R.drawable.ic_usuario,
                onValueChange = { nombre = it }
            )

            EditarCampoEditable(
                label = "Teléfono",
                value = telefono,
                icon = R.drawable.ic_telefono,
                onValueChange = { telefono = it }
            )

            EditarCampoEditable(
                label = "Direccion",
                value = direccion,
                icon = R.drawable.ic_ubicacion,
                onValueChange = { direccion = it }
            )

            Spacer(modifier = Modifier.height(30.dp))


            // ==========================================================
            // BOTÓN GUARDAR
            // ==========================================================
            Button(
                onClick = {
                    // Convertir la imagen a Base64 si se seleccionó nueva
                    val fotoBase64 = imageUri?.let { uri ->
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            val bytes = input.readBytes()
                            Base64.encodeToString(bytes, Base64.DEFAULT)
                        }
                    } ?: usuario.foto

                    val actualizado = usuario.copy(
                        nombre = nombre,
                        telefono = telefono,
                        direccion = direccion,
                        foto = fotoBase64
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            // 1️⃣ Guardar siempre en local
                            userRepository.actualizarLocal(actualizado)
                            Log.d("BACKEND_TEST", "Usuario actualizado localmente")

                            // 2️⃣ Si hay internet, intentar actualizar en remoto
                            if (tieneInternet(context)) {
                                val exitoRemoto = userRepository.actualizarRemoto(actualizado)
                                Log.d("BACKEND_TEST", "Actualización remota: $exitoRemoto")
                            } else {
                                Log.d("BACKEND_TEST", "No hay internet, usuario guardado solo local")
                            }

                            // 3️⃣ Volver al menú en Main dispatcher
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Cambios guardados", Toast.LENGTH_SHORT).show()
                                navController.navigate("menu") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }

                        } catch (e: Exception) {
                            Log.e("BACKEND_TEST", "Error guardando usuario: ${e.message}")
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Error al guardar cambios", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(gradient),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Guardar cambios", color = Color.White, fontSize = 16.sp)
                }
            }


            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}


@Composable
fun EditarCampoEditable(
    label: String,
    value: String,
    icon: Int,
    onValueChange: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {

        Text(
            label,
            color = Color(0xFF4A4A4A),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .background(Color(0xFFF3F3F3), RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(35.dp)
                    .background(Color(0xFF053C5E), RoundedCornerShape(40.dp)),
                contentAlignment = Alignment.Center
            ) {

                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color(0xFF053C5E)
                ),
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 16.sp,
                    color = Color.Black
                )
            )
        }
    }
}
