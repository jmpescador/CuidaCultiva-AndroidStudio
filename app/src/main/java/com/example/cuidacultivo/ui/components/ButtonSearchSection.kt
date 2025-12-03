package com.example.cuidacultivo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.cuidacultivo.R

@Composable
fun ButtonSearchSection(
    onGalleryClick: () -> Unit,
    onCameraClick: () -> Unit,
    onSearchClick: () -> Unit
) {

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1976D2),
            Color(0xFF002E4A)
        )
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 14.dp, top = 14.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(gradient)
            .padding(vertical = 12.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        val buttonSize = 60.dp
        val iconSize = 48.dp
        val iconScale = 1.5f
        val buttonColor = Color(0xCC002E4A)

        // ---- BOTÓN GALERÍA ----
        Button(
            onClick = onGalleryClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = Color.White
            ),
            shape = CircleShape,
            modifier = Modifier.size(buttonSize)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_galeria),
                contentDescription = "Abrir galería",
                modifier = Modifier
                    .size(iconSize)
                    .graphicsLayer(scaleX = iconScale, scaleY = iconScale)
            )
        }

        // ---- BOTÓN CÁMARA ----
        Button(
            onClick = onCameraClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = Color.White
            ),
            shape = CircleShape,
            modifier = Modifier.size(buttonSize)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_camara),
                contentDescription = "Tomar foto",
                modifier = Modifier
                    .size(iconSize)
                    .graphicsLayer(scaleX = iconScale, scaleY = iconScale)
            )
        }

        // ---- BOTÓN BUSCAR ----
        Button(
            onClick = onSearchClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor,
                contentColor = Color.White
            ),
            shape = CircleShape,
            modifier = Modifier.size(buttonSize)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_lupa),
                contentDescription = "Buscar",
                modifier = Modifier
                    .size(iconSize)
                    .graphicsLayer(scaleX = iconScale, scaleY = iconScale)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ButtonSearchSectionPreview() {
    ButtonSearchSection({}, {}, {})
}
