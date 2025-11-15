package com.example.storeapp.data.remote

// Esto es lo que enviaremos a la API para iniciar sesión
data class LoginRequest(
    val username: String,
    val password: String
)

// Esto es lo que la API nos devolverá si el inicio de sesión es exitoso
data class LoginResponse(
    val token: String
)