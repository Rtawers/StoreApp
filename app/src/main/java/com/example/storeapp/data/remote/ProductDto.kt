package com.example.storeapp.data.remote

import com.google.gson.annotations.SerializedName

// Esta es la data class principal que representa un producto.
data class ProductDto(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: RatingDto // Aquí hacemos referencia a la otra data class.
)

// Esta es la data class anidada para el objeto "rating".
// La ponemos en el mismo archivo porque está muy relacionada.
data class RatingDto(
    val rate: Double,
    @SerializedName("count")
    val reviewCount: Int
)