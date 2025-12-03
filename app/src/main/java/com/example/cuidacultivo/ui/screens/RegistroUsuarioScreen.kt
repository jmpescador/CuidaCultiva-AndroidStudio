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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.cuidacultivo.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.cuidacultivo.ui.components.menu.LayoutMenu

// -----------------------------
// Pantalla principal de registro de usuario
// -----------------------------
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // FOTO DE PERFIL
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .clickable {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_MEDIA_IMAGES
                            ) == PackageManager.PERMISSION_GRANTED
                        ) pickImageLauncher.launch("image/*")
                        else permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUri),
                        contentDescription = "Foto seleccionada",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(CircleShape)
                    )
                } else Text("Seleccionar foto")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // CAMPOS DE REGISTRO
            CampoRegistro("Nombre", nombre, R.drawable.ic_usuario) { nombre = it }
            CampoRegistro("Teléfono", telefono, R.drawable.ic_telefono) { telefono = it }
            CampoRegistro("Cédula", cedula, R.drawable.ic_usuario) { cedula = it }
            CampoRegistro("Dirección", direccion, R.drawable.ic_ubicacion) { direccion = it }

            Spacer(modifier = Modifier.height(10.dp))

            // BOTÓN REGISTRAR
            Button(
                onClick = {
                    if (nombre.isNotEmpty() && telefono.isNotEmpty() &&
                        cedula.isNotEmpty() && direccion.isNotEmpty()
                    ) {
                        onRegister(nombre, telefono, cedula, direccion, imageUri)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar usuario")
            }
        }
    }
}

@Composable
fun CampoRegistro(
    label: String,
    value: String,
    icon: Int,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Text(label, color = Color(0xFF4A4A4A), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(bottom = 6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .background(Color(0xFFF3F3F3), RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(35.dp).background(Color(0xFF053C5E), RoundedCornerShape(40.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier.size(20.dp))
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
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = Color.Black)
            )
        }
    }
}


// -----------------------------
// Preview
// -----------------------------
@Preview(showBackground = true)
@Composable
fun RegistroUsuarioScreenPreview() {
    RegistroUsuarioScreen(
        onRegister = { _, _, _, _, _ -> }
    )
}
