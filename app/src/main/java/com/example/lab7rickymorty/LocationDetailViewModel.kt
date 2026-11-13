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

class LocationDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val locationId: Int = savedStateHandle.toRoute<LocationDetail>().id

    private val _uiState = MutableStateFlow(UiState<Location>())
    val uiState: StateFlow<UiState<Location>> = _uiState

    init {
        loadLocation()
    }

    fun loadLocation() {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            val location = locationRepository.getLocationById(locationId)
            _uiState.value = UiState(isLoading = false, data = location)
        }
    }

    fun setError() {
        _uiState.value = _uiState.value.copy(isLoading = false, hasError = true)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LocationDetailViewModel(
                    savedStateHandle = createSavedStateHandle(),
                    locationRepository = appContainer().locationRepository
                )
            }
        }
    }
}
