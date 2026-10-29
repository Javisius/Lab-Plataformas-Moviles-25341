package com.example.lab7rickymorty

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class CharactersViewModel(application: Application) : AndroidViewModel(application) {

    private val characterDao = AppDatabase.getDatabase(application).characterDao()

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
                characterDao.getAll(),
                _showOnlyFavorites
            ) { entities, onlyFavorites ->
                val items = entities.map { it.toListItem() }
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
            characterDao.updateFavorite(id, !currentValue)
        }
    }

    fun setError() {
        _uiState.value = _uiState.value.copy(isLoading = false, hasError = true)
    }
}