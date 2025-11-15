package com.example.storeapp.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Esta línea crea una extensión de Context para tener una única instancia de DataStore
// en toda la aplicación. Es la forma recomendada de instanciarlo.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {

    // Obtenemos la instancia de DataStore
    private val sessionDataStore = context.dataStore

    // Creamos la "llave" con la que guardaremos y leeremos nuestro token.
    // Es como el nombre de la columna en una base de datos.
    companion object {
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    /**
     * Guarda el token de autenticación en DataStore.
     * La función 'edit' es una transacción segura.
     */
    suspend fun saveAuthToken(token: String) {
        sessionDataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token
        }
    }

    /**
     * Borra el token de autenticación de DataStore.
     * Se usará para el "Cerrar Sesión".
     */
    suspend fun clearAuthToken() {
        sessionDataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
        }
    }

    /**
     * Expone un Flow que emite el token guardado.
     * Si no hay token, emitirá 'null'.
     * Usamos un Flow porque es reactivo: si el token cambia (alguien inicia o cierra sesión),
     * cualquier parte de la app que esté "escuchando" este Flow será notificada al instante.
     */
    val authTokenFlow: Flow<String?> = sessionDataStore.data.map { preferences ->
        preferences[AUTH_TOKEN_KEY]
    }
}