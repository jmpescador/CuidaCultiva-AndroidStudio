package com.example.cuidacultivo.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import com.example.cuidacultivo.ui.screens.MenuScreen
import com.example.cuidacultivo.ui.screens.EditarUsuarioScreen
import com.example.cuidacultivo.data.Usuario
import com.example.cuidacultivo.ui.screens.HomeScreen
import com.example.tuapp.ui.screens.ResultScreen

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // Pantalla principal
        composable("home") {
            HomeScreen(navController)
        }

        // Pantalla del menú
        composable("menu") {
            MenuScreen(navController)
        }

        // Pantalla de edición de usuario
        composable("editarUsuario") {
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

        // Pantalla resultado con argumento de plaga
        composable(
            route = "result/{plaga}",
            arguments = listOf(navArgument("plaga") { type = NavType.StringType })
        ) { backStackEntry ->
            val plagaDetectada = backStackEntry.arguments?.getString("plaga") ?: "Desconocida"
            ResultScreen(plagaDetectada = plagaDetectada, navController = navController)
        }
    }
}
