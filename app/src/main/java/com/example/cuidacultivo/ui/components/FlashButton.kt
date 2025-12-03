package com.example.cuidacultivo.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.cuidacultivo.R

@Composable
fun FlashButton(
    flashMode: FlashMode,
    onToggle: () -> Unit
) {
    val icon = when (flashMode) {
        FlashMode.OFF -> R.drawable.flash_off
        FlashMode.ON -> R.drawable.flash_on
        FlashMode.AUTO -> R.drawable.flash_auto
    }

    IconButton(onClick = onToggle) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "Flash Mode",
            tint = androidx.compose.ui.graphics.Color.White
        )
    }
}
