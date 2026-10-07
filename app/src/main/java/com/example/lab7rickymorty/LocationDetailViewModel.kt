package com.example.lab7rickymorty

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationDetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val locationId: Int = savedStateHandle.toRoute<LocationDetail>().id

    private val _uiState = MutableStateFlow(UiState<Location>())
    val uiState: StateFlow<UiState<Location>> = _uiState.asStateFlow()

    init {
        loadLocation()
    }

    fun loadLocation() {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            delay(2000)
            val location = LocationDb().getLocationById(locationId)
            _uiState.value = UiState(isLoading = false, data = location)
        }
    }

    fun setError() {
        _uiState.value = _uiState.value.copy(isLoading = false, hasError = true)
    }
}