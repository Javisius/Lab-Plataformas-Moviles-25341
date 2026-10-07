package com.example.lab7rickymorty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<Location>>())
    val uiState: StateFlow<UiState<List<Location>>> = _uiState.asStateFlow()

    init {
        loadLocations()
    }

    fun loadLocations() {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            delay(4000)
            val locations = LocationDb().getAllLocations()
            _uiState.value = UiState(isLoading = false, data = locations)
        }
    }

    fun setError() {
        _uiState.value = _uiState.value.copy(isLoading = false, hasError = true)
    }
}