package com.example.lab7rickymorty

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CharacterDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val characterId: Int = savedStateHandle.toRoute<CharacterDetail>().id

    private val _uiState = MutableStateFlow(UiState<CharacterListItem>())
    val uiState: StateFlow<UiState<CharacterListItem>> = _uiState

    init {
        loadCharacter()
    }

    fun loadCharacter() {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            val item = characterRepository.getCharacterById(characterId)
            _uiState.value = UiState(isLoading = false, data = item)
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val current = _uiState.value.data ?: return@launch
            characterRepository.setFavorite(characterId, !current.isFavorite)
            loadCharacter()
        }
    }

    fun setError() {
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
