package com.example.storeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.storeapp.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    // Esta es una "lambda". Es una función que la pantalla de Login llamará
    // cuando el inicio de sesión sea exitoso, para que el sistema de navegación
    // sepa que debe ir a la lista de productos.
    onLoginSuccess: () -> Unit,
    // hiltViewModel() se encarga de obtener la instancia correcta del ViewModel que creamos antes.
    viewModel: LoginViewModel = hiltViewModel()
) {
    // ---- ESTADOS DE LA UI ----

    // 'uiState' es el StateFlow que viene del ViewModel. 'collectAsState' lo convierte
    // en algo que Compose puede observar. Cada vez que el 'uiState' en el ViewModel cambie,
    // esta parte del código se "recompondrá" (se redibujará).
    val uiState by viewModel.uiState.collectAsState()

    // 'remember' se usa para que Compose "recuerde" el valor de estas variables
    // entre recomposiciones. 'mutableStateOf' crea un estado observable para Compose.
    var username by remember { mutableStateOf("mor_2314") } // Valor por defecto para pruebas
    var password by remember { mutableStateOf("83r5^_") } // Valor por defecto para pruebas

    // 'LaunchedEffect' es un Composable especial para ejecutar código suspendido (como una navegación)
    // en respuesta a un cambio de estado, de una manera segura dentro del ciclo de vida de Compose.
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }

    // ---- DISEÑO DE LA PANTALLA (LAYOUT) ----

    // Box es un contenedor que permite apilar elementos.
    // 'fillMaxSize' hace que ocupe toda la pantalla.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center // Centra el contenido
    ) {
        // Column apila los elementos verticalmente.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp), // Espacio entre elementos
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp) // Márgenes a los lados
        ) {
            Text("Iniciar Sesión", style = MaterialTheme.typography.headlineMedium)

            // Campo de texto para el usuario.
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de texto para la contraseña.
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(), // Oculta el texto
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            // Botón para iniciar sesión.
            Button(
                onClick = {
                    // Cuando se hace clic, llamamos a la función 'login' del ViewModel.
                    viewModel.login(username, password)
                },
                // El botón estará deshabilitado si la app está en estado de carga.
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            // ---- MANEJO DE ESTADOS DE CARGA Y ERROR ----

            // Si está cargando, mostramos un círculo de progreso.
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }

            // Si hay un mensaje de error, lo mostramos en rojo.
            uiState.error?.let { errorMsg ->
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}