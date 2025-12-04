package com.example.cuidacultivo.ui.screens
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cuidacultivo.tflite.runModelText // función que crearemos para procesar texto
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.cuidacultivo.R
import kotlin.collections.set

@Composable
fun BuscarPlagaScreen(
    navController: NavController,
    showBackButton: Boolean = true, // <-- Nuevo parámetro
) {
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(false) }

    val bottomMoonShape = GenericShape { size, _ ->
        moveTo(0f, 0f)
        lineTo(size.width, 0f)
        lineTo(size.width, size.height * 0.40f)

        cubicTo(
            size.width * 0.75f, size.height * 0.75f,
            size.width * 0.25f, size.height * 0.75f,
            0f, size.height * 0.40f
        )

        close()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // Header recortado en media luna hacia abajo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(bottomMoonShape)
        ) {

            Image(
                painter = painterResource(id = R.drawable.fondo_menu),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF063850), // azul intermedio
                                Color(0x99014A68), // intermedio 60%
                                Color(0x00014365)  // transparente abajo
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 45.dp, start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Flecha opcional
                if (showBackButton) {
                    Image(
                        painter = painterResource(id = R.drawable.flecha),
                        contentDescription = "Volver",
                        modifier = Modifier
                            .size(36.dp)
                            .offset(x = 12.dp, y = (-20).dp)
                            .clickable { navController.popBackStack() }
                    )
                } else {
                    Box(modifier = Modifier.size(36.dp)) // Mantener espacio
                }

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(140.dp)
                )

                Box(modifier = Modifier.size(36.dp))
            }
        }
        //contenido
        //contenido
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Título
            Text(
                text = "Búsqueda por Síntomas",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Barra de búsqueda
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_lupa),
                        contentDescription = null
                    )
                },
                placeholder = { Text("Describe los síntomas que ves...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Chips de síntomas comunes
            Text("Síntomas Comunes", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(10.dp))

            val sintomas = listOf(
                "Manchas amarillas en hojas",
                "Hojas marchitas",
                "Puntos negros",
                "Hojas enrolladas",
                "Agujeros en hojas",
                "Polvo blanco",
                "Hojas caídas",
                "Tallos blandos"
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                sintomas.forEach { sintoma ->
                    AssistChip(
                        onClick = { inputText = TextFieldValue(sintoma) },
                        label = { Text(sintoma, fontSize = 13.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Cuadro verde con lupa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .background(Color(0xFFE8F9EF), shape = MaterialTheme.shapes.medium)
                    .padding(vertical = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_lupa),
                        contentDescription = null,
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Describe lo que observas", fontSize = 18.sp, color = Color.Black)
                    Text(
                        "Escribe los síntomas o selecciona uno de los más comunes",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Botón Buscar que activa el modelo
            Button(
                onClick = {
                    if (inputText.text.isNotBlank()) {
                        isLoading = true
                        val jsonResult = runModelText(navController.context, inputText.text)

                        Handler(Looper.getMainLooper()).post {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                "plagaInfo",
                                jsonResult
                            )
                            navController.navigate("result")
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Buscar", fontSize = 16.sp)
                }
            }
        }
    }
}
