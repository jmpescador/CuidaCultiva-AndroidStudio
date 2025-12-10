package com.example.cuidacultivo.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.cuidacultivo.R
import com.example.cuidacultivo.data.Usuario
import com.example.cuidacultivo.data.UserRepository
import com.example.cuidacultivo.ui.components.menu.LayoutMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MenuScreen(navController: NavController) {
    val context = LocalContext.current
    var usuarioRecienRegistrado by remember { mutableStateOf<Usuario?>(null) }

    // 🔹 Cargar usuario desde Room al iniciar
    LaunchedEffect(Unit) {
        val repo = UserRepository(context)
        val usuario = withContext(Dispatchers.IO) { repo.obtenerUsuarioLocal() }
        usuarioRecienRegistrado = usuario
    }

    LayoutMenu(navController = navController) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            usuarioRecienRegistrado?.let { usuario ->
                UsuarioRow(usuario = usuario, navController = navController)
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 🔵 Opciones del menú
            MenuButton("Cámara de diagnóstico", R.drawable.ic_camara) {
                navController.navigate("home")
            }

            MenuButton("Historial de consultas", R.drawable.ic_history) {
                navController.navigate("historialConsulta")
            }

            MenuButton("Consultar plagas", R.drawable.ic_bug) {
                navController.navigate("wikiPlagas")
            }

            MenuButton("Agregar cultivo", R.drawable.ic_add_cultivo) {
                navController.navigate("agregarCultivo")
            }

            MenuButton("Centro de ayuda", R.drawable.ic_help) {
                navController.navigate("centroAyuda")
            }
        }
    }
}

@Composable
fun MenuButton(text: String, icon: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(30.dp))
            .clickable { onClick() }
            .padding(start = 10.dp, top = 10.dp, end = 20.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(Color(0xFF053C5E), RoundedCornerShape(50.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(text, fontSize = 16.sp)

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(id = R.drawable.flecha_lateral),
            contentDescription = "Ir",
            tint = Color(0xFF053C5E)
        )
    }

    Spacer(modifier = Modifier.height(15.dp))
}

@Composable
fun UsuarioRow(usuario: Usuario, navController: NavController) {

    val bitmap = remember(usuario.foto) {
        usuario.foto?.let { base64String ->
            try {
                val bytes = Base64.decode(base64String, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) { null }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(30.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 📸 FOTO (50dp igual al diseño)
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto del usuario",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(50.dp))
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.ic_usuario),
                contentDescription = "Foto del usuario",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(50.dp))
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // 📄 NOMBRE Y ROL
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = usuario.nombre,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Agricultor",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        // ✏️ ICONO EDITAR estilo mockup
        Icon(
            painter = painterResource(id = R.drawable.editar),
            contentDescription = "Editar",
            tint = Color(0xFF053C5E),
            modifier = Modifier
                .size(22.dp)
                .clickable {
                    navController.currentBackStackEntry?.savedStateHandle?.set("usuario", usuario)
                    navController.navigate("editarUsuario")
                }
        )
    }
}

