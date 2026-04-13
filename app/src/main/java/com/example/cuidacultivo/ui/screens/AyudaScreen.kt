package com.example.cuidacultivo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cuidacultivo.ui.components.menu.LayoutMenu

// -----------------------------------------------------------
// PANTALLA DE AYUDA
// -----------------------------------------------------------
@Composable
fun AyudaScreen(
    navController: NavController,
    showBackButton: Boolean = true
) {

    LayoutMenu(navController = navController, showBackButton = showBackButton) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                item {
                    AyudaCard(
                        titulo = "¿Cómo detectar plagas?",
                        descripcion = "Toma una foto clara del cultivo desde la app en la sección 'Camara de Diagnostico'. El sistema analizará la imagen automáticamente."
                    )
                }

                item {
                    AyudaCard(
                        titulo = "Ver resultados",
                        descripcion = "Después de analizar la imagen, podrás ver el nombre de la plaga y el porcentaje de confianza."
                    )
                }

                item {
                    AyudaCard(
                        titulo = "Historial de consultas",
                        descripcion = "En el historial podrás revisar todas las consultas realizadas anteriormente."
                    )
                }

                item {
                    AyudaCard(
                        titulo = "Consultar Plagas",
                        descripcion = "En la seccion de Consultar plagas podras ver una wiki con las plagas registradas y una breve informacion sobre cada una de ellas."
                    )
                }

                item {
                    AyudaCard(
                        titulo = "Consultar Por Sintoma",
                        descripcion = "En la seccion de camara de diagnostica en la parte de abajo seleccionas la lupa en esa lugar describes los sintomas que tiene la planta y te pasara posibles plagas."
                    )
                }

                item {
                    AyudaCard(
                        titulo = "Editar usuario",
                        descripcion = "Puedes modificar tu información personal desde el menú principal en la opción 'Editar Usuario'."
                    )
                }

                item {
                    AyudaCard(
                        titulo = "Contacto",
                        descripcion = "Si necesitas ayuda puedes comunicarte al correo soporte@cuidacultivo.com o al número +57 300 123 4567."
                    )
                }

            }
        }
    }
}

// -----------------------------------------------------------
// CARD DE AYUDA (REUTILIZABLE)
// -----------------------------------------------------------
@Composable
fun AyudaCard(
    titulo: String,
    descripcion: String
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1976D2), Color(0xFF002E4A))
                            )
                        )
                        .clickable { expanded = !expanded }
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = if (expanded) "Ver menos" else "Ver más",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}