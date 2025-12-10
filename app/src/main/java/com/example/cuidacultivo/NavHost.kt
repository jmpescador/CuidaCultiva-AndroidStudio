package com.example.cuidacultivo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.room.Room
import com.example.cuidacultivo.data.AppDatabase
import com.example.cuidacultivo.data.HistorialDao
import com.example.cuidacultivo.data.Usuario
import com.example.cuidacultivo.ui.screens.BuscarPlagaScreen
import com.example.cuidacultivo.ui.screens.EditarUsuarioScreen
import com.example.cuidacultivo.ui.screens.HistorialConsultaScreen
import com.example.cuidacultivo.ui.screens.HomeScreen
import com.example.cuidacultivo.ui.screens.MenuScreen
import com.example.cuidacultivo.ui.screens.WikiPlagasScreen
import com.example.tuapp.ui.screens.ResultScreen

object Routes {
    const val HOME = "home"
    const val MENU = "menu"
    const val EDITAR_USUARIO = "editarUsuario"
    const val RESULT = "result"
    const val BUSCAR_PLAGA = "buscarPlaga"

    const val WIKI_PLAGAS = "wikiPlagas"
    const val HISTORIAL_CONSULTA = "historialConsulta"
}

@Composable
fun AppNavigation(navController: NavHostController) {

    val context = LocalContext.current

    // Instancia de Room
    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "cuidacultivo_db"
    ).build()
    val dao: HistorialDao = db.historialDao()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {

        composable(Routes.HOME) { HomeScreen(navController) }
        composable(Routes.MENU) { MenuScreen(navController) }

        composable(Routes.EDITAR_USUARIO) {
            val usuario = navController.previousBackStackEntry
                ?.savedStateHandle
                ?.get<Usuario>("usuario")

            usuario?.let {
                EditarUsuarioScreen(
                    navController = navController,
                    usuario = it,
                    onGuardarCambios = { usuarioActualizado ->
                        // Actualiza la base de datos o refresca el menú
                    }
                )
            }
        }

        composable(Routes.RESULT) { ResultScreen(navController) }
        composable(Routes.BUSCAR_PLAGA) { BuscarPlagaScreen(navController) }
        composable(Routes.WIKI_PLAGAS) { WikiPlagasScreen(navController) }

        // Historial pasando el DAO
        composable(Routes.HISTORIAL_CONSULTA) {
            HistorialConsultaScreen(
                navController = navController,
                context = context, // <-- importante
                showBackButton = true
            )
        }

    }
}
