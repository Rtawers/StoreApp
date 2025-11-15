package com.example.storeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    // Obtiene todos los items. Devuelve un Flow para que la UI se actualice sola si algo cambia.
    @Query("SELECT * FROM cart_items")
    fun getAllItems(): Flow<List<CartItem>>

    // Inserta un item. Si ya existe (mismo ID), lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CartItem)

    // Actualiza solo la cantidad de un producto específico
    @Query("UPDATE cart_items SET quantity = :quantity WHERE id = :itemId")
    suspend fun updateQuantity(itemId: Int, quantity: Int)

    // Borra un producto por su ID
    @Query("DELETE FROM cart_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Int)

    // Borra todo el carrito (para cuando se finaliza la compra)
    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}