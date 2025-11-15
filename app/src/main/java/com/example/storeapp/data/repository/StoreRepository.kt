package com.example.storeapp.data.repository

import com.example.storeapp.data.local.CartDao
import com.example.storeapp.data.local.CartItem
import com.example.storeapp.data.remote.ApiService
import com.example.storeapp.data.remote.LoginRequest
import javax.inject.Inject
import javax.inject.Singleton

// @Singleton: Le dice a Hilt que solo debe existir una única instancia de este Repositorio en toda la app.
@Singleton
// @Inject constructor(...): Esta es la magia de Hilt. Le decimos: "Para construir un StoreRepository,
// necesitas un ApiService y un CartDao". Hilt buscará en su módulo (AppModule) cómo proveerlos y
// los "inyectará" automáticamente aquí.
class StoreRepository @Inject constructor(
    private val apiService: ApiService,
    private val cartDao: CartDao
) {

    // --- Funciones de Red (de ApiService) ---

    suspend fun login(username: String, password: String) =
        apiService.login(LoginRequest(username, password))

    suspend fun getProducts() = apiService.getAllProducts()


    // --- Funciones de Base de Datos (de CartDao) ---

    fun getCartItems() = cartDao.getAllItems()

    suspend fun addToCart(product: CartItem) = cartDao.insertItem(product)

    suspend fun updateCartItemQuantity(itemId: Int, quantity: Int) =
        cartDao.updateQuantity(itemId, quantity)

    suspend fun deleteCartItem(itemId: Int) = cartDao.deleteItemById(itemId)

    suspend fun clearCart() = cartDao.clearCart()
}