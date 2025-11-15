package com.example.storeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storeapp.data.repository.StoreRepository
import com.example.storeapp.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject



// Esta clase representa el "estado" de la pantalla de Login en un momento dado.
// Hacemos que sus propiedades sean inmutables (val) para promover un flujo de datos unidireccional.
data class LoginUiState(
    val isLoading: Boolean = false,    // Para saber si mostrar una barra de progreso.
    val loginSuccess: Boolean = false, // Para saber si la navegación a la siguiente pantalla debe ocurrir.
    val error: String? = null          // Para mostrar un mensaje de error si algo falla.
)


// @HiltViewModel: Le dice a Hilt que esta es una clase ViewModel y que puede inyectarle dependencias.
@HiltViewModel
// Usamos @Inject constructor para que Hilt nos provea automáticamente el Repositorio y el SessionManager.
class LoginViewModel @Inject constructor(
    private val repository: StoreRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // _uiState: Es un StateFlow PRIVADO y mutable. Solo el ViewModel puede cambiar su valor.
    private val _uiState = MutableStateFlow(LoginUiState())

    // uiState: Es la versión PÚBLICA e inmutable (de solo lectura) del StateFlow.
    val uiState = _uiState.asStateFlow()

    /**
     * Esta función es llamada por la UI cuando el usuario presiona el botón de iniciar sesión.
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            // --- 1. Estado de Carga ---
            _uiState.update { currentState ->
                currentState.copy(isLoading = true, error = null)
            }

            try {
                // Llamamos al repositorio para hacer la petición de red.
                val response = repository.login(username, password)

                if (response.isSuccessful && response.body() != null) {

                    // --- 2. Estado de Éxito ---
                    val token = response.body()!!.token
                    sessionManager.saveAuthToken(token)

                    _uiState.update {
                        it.copy(isLoading = false, loginSuccess = true)
                    }

                } else {
                    // --- 3. Error controlado ---
                    val errorBody = response.errorBody()?.string() ?: "Credenciales inválidas"

                    _uiState.update {
                        it.copy(isLoading = false, error = errorBody)
                    }
                }
            } catch (e: Exception) {
                // --- 4. Error inesperado ---
                _uiState.update {
                    it.copy(isLoading = false, error = "No se pudo conectar al servidor.")
                }
            }
        }
    }
}
