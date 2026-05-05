package com.example.cuidacultivo.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.cuidacultivo.R
import com.example.cuidacultivo.ui.components.menu.LayoutMenu

@Composable
fun RegistroUsuarioScreen(
    onRegister: (String, String, String, String, Uri?) -> Unit
) {

    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var cedula by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) pickImageLauncher.launch("image/*")
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1976D2),
            Color(0xFF002E4A)
        )
    )

    LayoutMenu(navController = rememberNavController(), showBackButton = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // ✅ SCROLL
                .imePadding() // ✅ evita que el teclado tape
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // FOTO
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(brush = gradient) // 🔥 aquí el gradiente
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
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.ic_usuario),
                        contentDescription = "Foto de usuario",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize(0.7f) // 👈 imagen más pequeña
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            CampoRegistro("Nombre", nombre, R.drawable.ic_usuario, 30) { nombre = it }

            CampoRegistro("Teléfono", telefono, R.drawable.ic_telefono, 10, KeyboardType.Number) {
                telefono = it
            }

            CampoRegistro("Cédula", cedula, R.drawable.ic_usuario, 10, KeyboardType.Number) {
                cedula = it
            }

            CampoRegistro("Dirección", direccion, R.drawable.ic_ubicacion, 50) {
                direccion = it
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BOTÓN GRADIENTE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(gradient)
                    .clickable {
                        if (nombre.isNotEmpty() && telefono.isNotEmpty() &&
                            cedula.isNotEmpty() && direccion.isNotEmpty()
                        ) {
                            onRegister(nombre, telefono, cedula, direccion, imageUri)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Registrar usuario",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp)) // 👈 espacio final
        }
    }
}

@Composable
fun CampoRegistro(
    label: String,
    value: String,
    icon: Int,
    maxLength: Int,
    keyboardType: KeyboardType = KeyboardType.Text,
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
                onValueChange = {
                    if (it.length <= maxLength) {
                        onValueChange(it)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = Color(0xFF053C5E)
                ),
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black)
            )
        }

        Text(
            text = "${value.length}/$maxLength",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegistroUsuarioScreenPreview() {
    RegistroUsuarioScreen { _, _, _, _, _ -> }
}