package com.example.cuidacultivo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cuidacultivo.data.Usuario
import com.example.cuidacultivo.ui.screens.BuscarPlagaScreen
import com.example.cuidacultivo.ui.screens.EditarUsuarioScreen
import com.example.cuidacultivo.ui.screens.HomeScreen
import com.example.cuidacultivo.ui.screens.MenuScreen
import com.example.tuapp.ui.screens.ResultScreen

// Definimos las rutas como constantes
object Routes {
    const val HOME = "home"
    const val MENU = "menu"
    const val EDITAR_USUARIO = "editarUsuario"
    const val RESULT = "result"
    const val BUSCAR_PLAGA = "buscarPlaga"
}

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {

        // Pantalla principal
        composable(Routes.HOME) {
            HomeScreen(navController)
        }

        // Pantalla del menú
        composable(Routes.MENU) {
            MenuScreen(navController)
        }

        // Pantalla de edición de usuario
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

        // Pantalla de resultado
        composable(Routes.RESULT) {
            ResultScreen(navController = navController)
        }

        // Pantalla de búsqueda de plaga
        composable(Routes.BUSCAR_PLAGA) {
            BuscarPlagaScreen(navController = navController)
        }
    }
}
