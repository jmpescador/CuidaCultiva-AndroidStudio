package com.example.cuidacultivo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.cuidacultivo.R
import com.example.cuidacultivo.ui.components.menu.LayoutMenu
import com.example.cuidacultivo.ui.screens.CampoRegistro
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PasswordScreen(
    onValidar: suspend (String) -> Boolean // 👈 validación externa (backend)
) {

    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("La contraseña es incorrecta") }

    val scope = rememberCoroutineScope()
    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF1976D2), Color(0xFF002E4A))
    )

    LayoutMenu(
        navController = rememberNavController(),
        showBackButton = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Acceso al sistema",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Ingrese la contraseña del grupo",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🔐 Campo de contraseña
            CampoRegistro(
                label = "Contraseña",
                value = password,
                icon = R.drawable.ic_usuario,
                maxLength = 20
            ) {
                password = it
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔘 Botón de login
            Button(
                onClick = {
                    if (password.isNotEmpty() && !loading) {
                        scope.launch {
                            try {
                                loading = true

                                val isValid = onValidar(password)

                                if (!isValid) {
                                    errorMessage = "La contraseña es incorrecta"
                                    showError = true
                                    password = ""
                                }

                            } catch (e: Exception) {
                                errorMessage = "Error de conexión con el servidor"
                                showError = true
                            } finally {
                                loading = false
                            }
                        }
                    } else {
                        errorMessage = "Debe ingresar una contraseña"
                        showError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent // 👈 importante
                ),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                        .background(gradient),
                    contentAlignment = Alignment.Center
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White, // 👈 para que se vea bien
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Ingresar",
                            color = Color.White
                        )
                    }
                }
            }

            // 🚨 Alerta de error
            if (showError) {
                AlertDialog(
                    onDismissRequest = { showError = false },
                    confirmButton = {
                        Button(
                            onClick = { showError = false }
                        ) {
                            Text("Aceptar")
                        }
                    },
                    title = {
                        Text("Error")
                    },
                    text = {
                        Text(errorMessage)
                    }
                )
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PasswordScreenPreview() {

    MaterialTheme {
        PasswordScreen(
            onValidar = { password ->
                // 🔥 Simulación: solo acepta "1234"
                password == "1234"
            }
        )
    }
}