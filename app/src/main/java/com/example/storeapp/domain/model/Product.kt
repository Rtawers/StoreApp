package com.example.storeapp.domain.model

// Esta es la clase que nuestra UI y ViewModels usarán.
// Es nuestro modelo de dominio interno.
data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String
)