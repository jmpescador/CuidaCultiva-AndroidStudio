package com.example.cuidacultivo.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cuidacultivo.data.AppDatabase
import com.example.cuidacultivo.data.HistorialConsulta
import com.example.cuidacultivo.data.HistorialDao
import com.example.cuidacultivo.ui.components.menu.LayoutMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// -----------------------------------------------------------
// HISTORIAL DE CONSULTAS
// -----------------------------------------------------------
@Composable
fun HistorialConsultaScreen(
    navController: NavController,
    context: Context,
    showBackButton: Boolean = true
) {
    // Obtener DAO usando la misma instancia de DB
    val dao: HistorialDao = remember {
        AppDatabase.getDatabase(context).historialDao()
    }

    // Estado que contendrá la lista de historial
    var historialLocal by remember { mutableStateOf(listOf<HistorialConsulta>()) }

    // Estado de búsqueda (opcional)
    var search by remember { mutableStateOf("") }

    // Cargar historial desde Room
    LaunchedEffect(Unit) {
        historialLocal = withContext(Dispatchers.IO) {
            val lista = dao.getAll() // trae todos los registros
            Log.d("HIST_LOCAL", "Cantidad de registros: ${lista.size}")
            lista
        }
    }

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
                val filtered = if (search.isEmpty()) {
                    historialLocal
                } else {
                    historialLocal.filter {
                        it.plagaDetectada.contains(search, ignoreCase = true)
                    }
                }

                items(filtered) { historial ->
                    HistorialCard(historial = historial)
                }
            }
        }
    }
}

@Composable
fun HistorialCard(historial: HistorialConsulta) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                // Imagen con placeholder
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray)
                        .padding(end = 12.dp)
                ) {
                    historial.imagenPath?.let { path ->
                        val bitmap = loadBitmapFromPath(path)
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = historial.plagaDetectada,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray)
                            )
                        }
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = historial.plagaDetectada,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Confianza: ${historial.porcentaje}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
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
                Spacer(modifier = Modifier.height(8.dp))
                Text("Método: ${historial.metodo}")
                Spacer(modifier = Modifier.height(6.dp))
                Text("Usuario: ${historial.usuarioCedula}")
                Spacer(modifier = Modifier.height(6.dp))
                Text("Fecha: ${formatDate(historial.fecha)}")
            }
        }
    }
}

// Función auxiliar para cargar imagen
fun loadBitmapFromPath(path: String): android.graphics.Bitmap? {
    return try {
        BitmapFactory.decodeFile(File(path).absolutePath)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
