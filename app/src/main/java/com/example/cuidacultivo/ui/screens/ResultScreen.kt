package com.example.cuidacultivo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.cuidacultivo.R
import org.json.JSONObject

@Composable
fun ResultScreen(
    navController: NavHostController,
    showBackButton: Boolean = true
) {

    // --- 1. Recuperar y procesar JSON ---
    val jsonStr = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("plagaInfo")

    val json = jsonStr?.let { JSONObject(it) }
        ?: JSONObject(
            """
            {
                "id": "unknown",
                "nombre":"Sin datos",
                "alias":"-",
                "probabilidad":0,
                "descripcion":"Sin descripción",
                "sintomas":"Sin síntomas",
                "control":"Sin control sugerido"
            }
            """.trimIndent()
        )

    val id = json.optString("id")
    val nombre = json.optString("nombre")
    val alias = json.optString("alias")
    val probabilidad = json.optDouble("probabilidad")
    val descripcion = json.optString("descripcion")
    val sintomas = json.optString("sintomas")
    val control = json.optString("control")

    // --- 2. Lógica de Estado (¿Se detectó algo o no?) ---
    // Si el ID es 'unknown' o el nombre es el mensaje de error, cambiamos el tema
    val isUnknown = id == "unknown" || nombre == "No se detectó plaga"

    // Colores dinámicos según el resultado
    val mainColor = if (isUnknown) Color(0xFF616161) else Color(0xFF1976D2) // Gris Oscuro vs Azul
    val secondaryColor = if (isUnknown) Color(0xFF212121) else Color(0xFF002E4A)

    // El gradiente del botón y decoraciones
    val themeGradient = Brush.verticalGradient(
        listOf(mainColor, secondaryColor)
    )

    // --- 3. Forma curva del header ---
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

        // ------------------ HEADER ------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(bottomMoonShape)
        ) {

            // Imagen de fondo (puedes cambiarla si es error, o dejar la misma)
            Image(
                painter = painterResource(id = R.drawable.fondo_menu),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Overlay de color (Aquí aplicamos el cambio de color dinámico)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                mainColor.copy(alpha = 0.8f),
                                secondaryColor.copy(alpha = 0.9f)
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

                // ← Botón Volver
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
                    Box(modifier = Modifier.size(36.dp))
                }

                // Logo
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(140.dp)
                )

                Box(modifier = Modifier.size(36.dp))
            }
        }

        // ------------------ CONTENIDO ------------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = (-60).dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // --- TARJETA PRINCIPAL (RESULTADO) ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF3F3F3))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {

                        // Nombre de la plaga
                        Text(
                            text = nombre,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = if (isUnknown) Color.Red.copy(alpha = 0.7f) else Color.Black,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Alias (Solo si existe)
                        if (alias.isNotEmpty() && alias != "-") {
                            Text(text = "Alias: $alias", fontSize = 16.sp, color = Color.DarkGray)
                        }

                        // Probabilidad
                        Text(
                            text = "Confianza: ${String.format("%.1f", probabilidad)}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (probabilidad > 80) Color(0xFF2E7D32) else Color.Gray // Verde si es alto
                        )
                    }
                }

                // --- SECCIÓN DETALLE ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF7F7F7))
                        .padding(16.dp)
                ) {

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

                        if (isUnknown) {
                            // Mensaje especial si no se detectó nada
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("⚠️ ", fontSize = 18.sp)
                                Text("Recomendación:", fontWeight = FontWeight.Bold)
                            }
                            Text(descripcion) // Aquí vendrá el texto de "Intenta tomar la foto..."

                        } else {
                            // Información normal de la plaga
                            Text("Descripción:", fontWeight = FontWeight.Bold, color = mainColor)
                            Text(descripcion)

                            Text("Síntomas:", fontWeight = FontWeight.Bold, color = mainColor)
                            Text(sintomas)

                            Text("Control:", fontWeight = FontWeight.Bold, color = mainColor)
                            Text(control)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- BOTÓN ---
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(themeGradient) // Usa el color dinámico (Gris o Azul)
                        .clickable { navController.popBackStack() }
                        .padding(vertical = 12.dp, horizontal = 24.dp)
                ) {
                    Text(
                        text = if (isUnknown) "Intentar de nuevo" else "Tomar otra foto",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}