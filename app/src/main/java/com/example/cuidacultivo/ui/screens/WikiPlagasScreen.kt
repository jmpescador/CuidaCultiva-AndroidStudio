package com.example.cuidacultivo.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.cuidacultivo.R
import com.example.cuidacultivo.data.PlagaInfo
import com.example.cuidacultivo.data.plagasMap

// -----------------------------------------------------------
// WIKI PLAGAS SCREEN
// -----------------------------------------------------------

@Composable
fun WikiPlagasScreen(
    navController: NavController,
    showBackButton: Boolean = true,
) {
    var search by remember { mutableStateOf("") }

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
                            listOf(
                                Color(0xFF063850),
                                Color(0x99014A68),
                                Color(0x00014365)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 45.dp, start = 20.dp, end = 20.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

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

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(140.dp)
                )

                Box(modifier = Modifier.size(36.dp))
            }
        }

        // ------------------ LISTA SCROLEABLE ------------------

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 20.dp) // <-- SIN PADDING
        ) {

            item {
                PlagasSearchBar(
                    text = search,
                    onTextChange = { search = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            val filteredList = plagasMap.values.filter {
                it.nombre.contains(search, ignoreCase = true) ||
                        it.alias.any { alias -> alias.contains(search, ignoreCase = true) }
            }

            items(filteredList) { plaga ->
                PlagaCard(plaga)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

// -----------------------------------------------------------
// TARJETA DE PLAGAS
// -----------------------------------------------------------
@Composable
fun PlagaCard(plaga: PlagaInfo) {
    var expanded by remember { mutableStateOf(false) }

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF1976D2), Color(0xFF002E4A))
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {

                Image(
                    painter = painterResource(id = plaga.imagenRes),
                    contentDescription = plaga.nombre,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .padding(end = 12.dp)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(
                        plaga.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        plaga.alias.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // ----------- BOTÓN CON GRADIENTE -----------
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(gradient)
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

            // ----------- CONTENIDO EXPANDIDO -----------
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))

                Text("Descripción: ${plaga.descripcion}")
                Spacer(modifier = Modifier.height(6.dp))

                Text("Síntomas: ${plaga.sintomas}")
                Spacer(modifier = Modifier.height(6.dp))

                Text("Control: ${plaga.control}")
            }
        }
    }
}

// -----------------------------------------------------------
// BARRA DE BÚSQUEDA
// -----------------------------------------------------------

@Composable
fun PlagasSearchBar(
    text: String,
    onTextChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(
                Color(0xFFF3F4F6),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Icon(
                painter = painterResource(id = R.drawable.ic_lupa),
                contentDescription = null,
                tint = Color.Gray.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                textStyle = LocalTextStyle.current.copy(
                    color = Color.Black,
                    fontSize = 16.sp
                ),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text(
                            "Describe los síntomas que ves...",
                            color = Color.Gray.copy(alpha = 0.6f),
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}
