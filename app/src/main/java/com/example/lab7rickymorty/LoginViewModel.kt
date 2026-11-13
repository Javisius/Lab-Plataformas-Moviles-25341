package com.example.lab7rickymorty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

// Estado propio del login: además de cargando/éxito necesita el texto del error
data class LoginUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false
)

class LoginViewModel(
    private val userPreferences: UserPreferences,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // Se usa tanto para "Iniciar sesión" como para "Reintentar"
    fun login(name: String) {
        if (_uiState.value.isLoading) return

        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)

            try {
                // Primero los datos: si cualquiera falla, salta al catch
                // y el nombre NO se guarda (la sesión no queda iniciada)
                characterRepository.syncIfEmpty()
                locationRepository.syncIfEmpty()

                userPreferences.saveName(name.trim())
                _uiState.value = LoginUiState(isLoggedIn = true)
            } catch (e: IOException) {
                // Sin internet, DNS que no resuelve, timeout...
                _uiState.value = LoginUiState(
                    errorMessage = "Sin conexión a internet. Revisa tu red e inténtalo de nuevo."
                )
            } catch (e: HttpException) {
                // El servidor respondió, pero con un código de error
                val message = if (e.code() == 429) {
                    // 429 Too Many Requests: el API limita cuántas peticiones acepta por IP
                    "El servidor limitó las peticiones. Cambia de red o inténtalo más tarde."
                } else {
                    "El servidor respondió con un error (${e.code()}). Inténtalo de nuevo."
                }
                _uiState.value = LoginUiState(errorMessage = message)
            } catch (e: SerializationException) {
                // La respuesta llegó, pero el JSON no tiene la forma que esperan los DTOs
                _uiState.value = LoginUiState(
                    errorMessage = "La respuesta del servidor no tiene el formato esperado. Inténtalo de nuevo."
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                LoginViewModel(
                    userPreferences = container.userPreferences,
                    characterRepository = container.characterRepository,
                    locationRepository = container.locationRepository
                )
            }
        }
    }
}
