package com.example.storeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.storeapp.ui.screens.CartScreen
import com.example.storeapp.ui.screens.LoginScreen
import com.example.storeapp.ui.screens.ProductListScreen

@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = AppScreens.LoginScreen.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(AppScreens.ProductListScreen.route) {
                        popUpTo(AppScreens.LoginScreen.route) { inclusive = true }
                    }
                }
            )
        }

        // --- MODIFICACIÓN DEL PASO 19.3 APLICADA AQUÍ ---
        composable(route = AppScreens.ProductListScreen.route) {
            ProductListScreen(
                navController = navController,
                // Le pasamos la acción de navegación para el logout
                onLogout = {
                    // Navegamos de vuelta a la pantalla de Login
                    navController.navigate(AppScreens.LoginScreen.route) {
                        // popUpTo(0) borra TODA la pila de navegación.
                        // Esto es crucial para que el usuario no pueda volver atrás
                        // a las pantallas protegidas después de cerrar sesión.
                        popUpTo(0)
                    }
                }
            )
        }

        composable(route = AppScreens.CartScreen.route) {
            CartScreen(onCheckout = {
                navController.popBackStack()
            })
        }
    }
}