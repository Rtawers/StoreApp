package com.example.storeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

// La palabra clave "data" es esencial.
// Las propiedades van DENTRO de los paréntesis.
@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey val id: Int,
    val title: String,
    val price: Double,
    val image: String,
    var quantity: Int
)