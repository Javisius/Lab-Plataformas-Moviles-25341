package com.example.lab7rickymorty

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CharacterDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val characterId: Int = savedStateHandle.toRoute<CharacterDetail>().id
    private val characterDao = AppDatabase.getDatabase(application).characterDao()

    private val _uiState = MutableStateFlow(UiState<CharacterListItem>())
    val uiState: StateFlow<UiState<CharacterListItem>> = _uiState

    init {
        loadCharacter()
    }

    fun loadCharacter() {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            val entity = characterDao.getById(characterId)
            _uiState.value = UiState(isLoading = false, data = entity.toListItem())
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val current = _uiState.value.data ?: return@launch
            characterDao.updateFavorite(characterId, !current.isFavorite)
            loadCharacter()
        }
    }

    fun setError() {
        _uiState.value = _uiState.value.copy(isLoading = false, hasError = true)
    }
}