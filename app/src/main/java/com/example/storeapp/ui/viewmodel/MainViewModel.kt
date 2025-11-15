package com.example.storeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storeapp.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// Una "sealed class" es perfecta para representar estados que son excluyentes entre sí.
// La sesión solo puede estar en uno de estos tres estados a la vez.
sealed class SessionState {
    object Loading : SessionState()    // Estado inicial, mientras verificamos.
    object LoggedIn : SessionState()   // El usuario tiene una sesión activa.
    object LoggedOut : SessionState()  // El usuario no tiene una sesión activa.
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    // Creamos el StateFlow para manejar el estado de la sesión.
    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState = _sessionState.asStateFlow()

    // El bloque 'init' se ejecuta automáticamente cuando el ViewModel es creado por primera vez.
    init {
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            // .first() es un operador de Flow que toma solo el primer valor emitido.
            // Es ideal aquí porque solo necesitamos saber el estado del token una vez, al inicio.
            val token = sessionManager.authTokenFlow.first()
            if (token.isNullOrBlank()) {
                _sessionState.value = SessionState.LoggedOut
            } else {
                _sessionState.value = SessionState.LoggedIn
            }
        }
    }
}