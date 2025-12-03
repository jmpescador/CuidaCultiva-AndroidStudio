package com.example.cuidacultivo.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.cuidacultivo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarMenu(
    navController: NavController,      // ← AGREGADO
    switchState: Boolean,
    onSwitchChange: (Boolean) -> Unit,
) {
    val green = Color(0xFF00C853)
    val purple = Color(0xFF9C27B0)

    Surface(
        color = Color.Transparent,
        shadowElevation = 0.dp,
    ) {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(start = 10.dp, top = 20.dp, end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // █████ MENÚ CON FONDO BLANCO + COLOR DINÁMICO █████
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable {
                                navController.navigate("menu")   // ← AQUÍ NAVEGA A MenuScreen
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menú",
                            tint = if (switchState) purple else green,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Logo centrado
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Logo",
                        modifier = Modifier.size(100.dp)
                    )

                    // Switch personalizado
                    CustomIconSwitch(
                        checked = switchState,
                        onCheckedChange = onSwitchChange
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White
            ),
            scrollBehavior = null
        )
    }
}

@Composable
fun CustomIconSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val green = Color(0xFF00C853)
    val purple = Color(0xFF9C27B0)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Icono izquierdo
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(
                    color = if (!checked) green else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.cafe),
                contentDescription = "Icono izquierdo",
                modifier = Modifier.size(15.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                    if (!checked) Color.White else purple
                )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Icono derecho
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(
                    color = if (checked) purple else Color.Transparent,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.mora),
                contentDescription = "Icono derecho",
                modifier = Modifier.size(15.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                    if (checked) Color.White else green
                )
            )
        }
    }
}

@Composable
fun GradientBlock(
    switchState: Boolean,
    modifier: Modifier = Modifier,
    height: Int = 160,
    reverse: Boolean = false
) {
    val green = Color(0xFF009E00)
    val purple = Color(0xFF71277A)

    val activeColor = if (switchState) purple else green

    val gradient = if (!reverse) {
        Brush.verticalGradient(
            colors = listOf(
                activeColor.copy(alpha = 0.95f),
                activeColor.copy(alpha = 0.55f),
                activeColor.copy(alpha = 0.0f)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                activeColor.copy(alpha = 0.0f),
                activeColor.copy(alpha = 0.55f),
                activeColor.copy(alpha = 0.95f)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .background(gradient)
    )
}
