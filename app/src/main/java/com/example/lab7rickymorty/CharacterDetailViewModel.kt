package com.example.lab7rickymorty

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharacterDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val characterId: Int = savedStateHandle.toRoute<CharacterDetail>().id

    private val _uiState = MutableStateFlow(UiState<CharacterListItem>())
    val uiState: StateFlow<UiState<CharacterListItem>> = _uiState.asStateFlow()

    // true cuando el personaje ya no existe en Room: la pantalla vuelve a la lista
    private val _notFound = MutableStateFlow(false)
    val notFound: StateFlow<Boolean> = _notFound.asStateFlow()

    private var observeJob: Job? = null

    init {
        loadCharacter()
    }

    // Observa Room: cualquier cambio en la fila (favorito, edición, borrado)
    // llega aquí automáticamente, sin recargar a mano
    fun loadCharacter() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            characterRepository.getCharacterById(characterId).collect { item ->
                if (item == null) {
                    _uiState.value = UiState(isLoading = false)
                    _notFound.value = true
                } else {
                    _uiState.value = UiState(isLoading = false, data = item)
                }
            }
        }
    }

    fun toggleFavorite() {
        val current = _uiState.value.data ?: return
        viewModelScope.launch {
            characterRepository.setFavorite(characterId, !current.isFavorite)
        }
    }

    // Solo borra en Room. NO navega: al desaparecer la fila, el Flow emite null,
    // se marca notFound y la pantalla vuelve atrás (una sola fuente de navegación)
    fun deleteCharacter() {
        viewModelScope.launch {
            characterRepository.deleteCharacter(characterId)
        }
    }

    fun setError() {
        // Se deja de observar para que una nueva emisión no tape el error
        observeJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = false, hasError = true)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // createSavedStateHandle() trae los argumentos de la ruta (CharacterDetail.id)
                CharacterDetailViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    characterRepository = appContainer().characterRepository
                )
            }
        }
    }
}
