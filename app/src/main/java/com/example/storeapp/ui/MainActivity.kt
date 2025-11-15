package com.example.storeapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.storeapp.ui.navigation.AppNavigation
import com.example.storeapp.ui.navigation.AppScreens
import com.example.storeapp.ui.theme.StoreAppTheme
import com.example.storeapp.ui.viewmodel.MainViewModel
import com.example.storeapp.ui.viewmodel.SessionState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Obtenemos la instancia del MainViewModel usando la delegación de KTX.
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StoreAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Recolectamos el estado de la sesión como un estado de Compose.
                    // La UI se recompondrá cuando este estado cambie.
                    val sessionState by mainViewModel.sessionState.collectAsState()

                    // Un 'when' es la forma perfecta de manejar una sealed class en Kotlin.
                    // Es exhaustivo, lo que significa que nos obliga a manejar todos los casos.
                    when (sessionState) {
                        is SessionState.Loading -> {
                            // Muestra una pantalla de carga mientras se toma la decisión.
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        is SessionState.LoggedIn -> {
                            // Si la sesión está iniciada, la pantalla de inicio es la lista de productos.
                            AppNavigation(startDestination = AppScreens.ProductListScreen.route)
                        }
                        is SessionState.LoggedOut -> {
                            // Si no, la pantalla de inicio es el Login.
                            AppNavigation(startDestination = AppScreens.LoginScreen.route)
                        }
                    }
                }
            }
        }
    }
}