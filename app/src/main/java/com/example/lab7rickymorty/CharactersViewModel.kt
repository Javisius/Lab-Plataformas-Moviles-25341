package com.example.lab7rickymorty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharactersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<Character>>())
    val uiState: StateFlow<UiState<List<Character>>> = _uiState.asStateFlow()

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            delay(4000)
            val characters = CharacterDb().getAllCharacters()
            _uiState.value = UiState(isLoading = false, data = characters)
        }
    }

    fun setError() {
        _uiState.value = _uiState.value.copy(isLoading = false, hasError = true)
    }
}