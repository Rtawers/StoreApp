package com.example.storeapp.ui.navigation

// Sealed class para definir las rutas de nuestra aplicación de forma segura.
sealed class AppScreens(val route: String) {
    object LoginScreen : AppScreens("login_screen")
    object ProductListScreen : AppScreens("product_list_screen")
    // Dejamos preparada la ruta para la pantalla del carrito
    object CartScreen : AppScreens("cart_screen")
}