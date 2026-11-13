package com.example.lab7rickymorty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CharactersViewModel(
    private val characterRepository: CharacterRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<CharacterListItem>>())
    val uiState: StateFlow<UiState<List<CharacterListItem>>> = _uiState.asStateFlow()

    private val _showOnlyFavorites = MutableStateFlow(false)
    val showOnlyFavorites: StateFlow<Boolean> = _showOnlyFavorites.asStateFlow()

    private var observeJob: Job? = null

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)

            combine(
                characterRepository.getCharacters(),
                _showOnlyFavorites
            ) { items, onlyFavorites ->
                if (onlyFavorites) items.filter { it.isFavorite } else items
            }.collect { filteredItems ->
                _uiState.value = UiState(isLoading = false, data = filteredItems)
            }
        }
    }

    fun toggleFavoritesFilter() {
        _showOnlyFavorites.value = !_showOnlyFavorites.value
    }

    fun toggleFavorite(id: Int, currentValue: Boolean) {
        viewModelScope.launch {
            characterRepository.setFavorite(id, !currentValue)
        }
    }

    fun setError() {
        observeJob?.cancel()
        _uiState.value = _uiState.value.copy(isLoading = false, hasError = true)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CharactersViewModel(appContainer().characterRepository)
            }
        }
    }
}
