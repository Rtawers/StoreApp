package com.example.storeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storeapp.data.local.CartItem
import com.example.storeapp.data.repository.StoreRepository
import com.example.storeapp.data.session.SessionManager
import com.example.storeapp.domain.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ------------------------------------------------------------
// Estado de la UI para la lista de productos
// ------------------------------------------------------------
data class ProductListUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null,
    val snackbarMessage: String? = null // <-- NUEVA LÍNEA AÑADIDA
)

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: StoreRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        println("--- ProductListViewModel CREADO ---")
        loadProducts()
    }

    private fun loadProducts() {
        println("--- loadProducts() INICIADO ---")
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val response = repository.getProducts()
                println("--- Respuesta API recibida. ¿Exitosa?: ${response.isSuccessful} ---")

                if (response.isSuccessful && response.body() != null) {
                    val productDtos = response.body()!!
                    println("--- API devolvió ${productDtos.size} productos ---")

                    val products = productDtos.map { dto ->
                        Product(
                            id = dto.id,
                            title = dto.title,
                            price = dto.price,
                            description = dto.description,
                            category = dto.category,
                            image = dto.image
                        )
                    }
                    _uiState.update { it.copy(isLoading = false, products = products) }
                } else {
                    println("--- ERROR API: Código ${response.code()} ---")
                    _uiState.update { it.copy(isLoading = false, error = "Error al cargar los productos") }
                }
            } catch (e: Exception) {
                println("--- EXCEPCIÓN loadProducts: ${e.message} ---")
                _uiState.update { it.copy(isLoading = false, error = "No hay conexión a internet") }
            }
        }
    }

    // ------------------------------------------------------------
    // NUEVA FUNCIÓN: Agregar producto al carrito
    // ------------------------------------------------------------
    fun addToCart(product: Product) {
        viewModelScope.launch {
            val cartItem = CartItem(
                id = product.id,
                title = product.title,
                price = product.price,
                image = product.image,
                quantity = 1
            )

            repository.addToCart(cartItem)

            // Actualizamos el estado para mostrar el mensaje en Snackbar
            _uiState.update {
                it.copy(snackbarMessage = "'${product.title}' fue agregado al carrito")
            }
        }
    }

    // ------------------------------------------------------------
    // LIMPIAR MENSAJE DEL SNACKBAR
    // ------------------------------------------------------------
    fun snackbarMessageShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearAuthToken()
        }
    }
}
