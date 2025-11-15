package com.example.storeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storeapp.data.local.CartItem
import com.example.storeapp.data.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// Estado de la UI para la pantalla del carrito
data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val checkoutSuccess: Boolean = false // Para saber cuándo navegar hacia atrás
)

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    // --- ¡Punto Clave! Observamos el Flow de Room ---
    // repository.getCartItems() nos da un Flow<List<CartItem>>.
    // Usamos 'map' para transformar esa lista en nuestro objeto CartUiState cada vez que cambie.
    // 'stateIn' convierte este Flow frío en un StateFlow caliente que la UI puede consumir de forma segura.
    val uiState: StateFlow<CartUiState> = repository.getCartItems().map { items ->
        CartUiState(
            cartItems = items,
            totalPrice = items.sumOf { it.price * it.quantity } // Calculamos el total
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CartUiState()
    )

    // --- ACCIONES DEL USUARIO ---

    fun onQuantityChange(itemId: Int, quantity: Int) {
        viewModelScope.launch {
            if (quantity > 0) {
                repository.updateCartItemQuantity(itemId, quantity)
            } else {
                // Si la cantidad es 0, eliminamos el item
                repository.deleteCartItem(itemId)
            }
        }
    }

    fun removeItemFromCart(itemId: Int) {
        viewModelScope.launch {
            repository.deleteCartItem(itemId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    fun checkout() {
        viewModelScope.launch {
            repository.clearCart()
            // Podríamos añadir una lógica de éxito aquí si fuera necesario
        }
    }
}