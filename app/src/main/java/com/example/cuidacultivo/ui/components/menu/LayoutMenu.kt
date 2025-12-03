package com.example.cuidacultivo.ui.components.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.cuidacultivo.R

@Composable
fun LayoutMenu(
    navController: NavController,
    showBackButton: Boolean = true, // <-- Nuevo parámetro
    content: @Composable ColumnScope.() -> Unit
) {

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .offset(y = (-60).dp),
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLayoutMenu() {
    val navController = rememberNavController()

    // Ejemplo con flecha visible
    LayoutMenu(navController = navController, showBackButton = true) {
        Text(
            text = "Contenido de ejemplo",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(
            text = "Aquí va tu contenido.",
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLayoutMenuSinFlecha() {
    val navController = rememberNavController()

    // Ejemplo sin flecha
    LayoutMenu(navController = navController, showBackButton = false) {
        Text(
            text = "Contenido sin flecha",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(
            text = "Aquí va tu contenido.",
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}