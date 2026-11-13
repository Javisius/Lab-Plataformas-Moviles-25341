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
import kotlinx.coroutines.launch

class LocationsViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<Location>>())
    val uiState: StateFlow<UiState<List<Location>>> = _uiState.asStateFlow()

    private var observeJob: Job? = null

    init {
        loadLocations()
    }

    fun loadLocations() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            locationRepository.getLocations().collect { locations ->
                _uiState.value = UiState(isLoading = false, data = locations)
            }
        }
    }

    fun setError() {
        _uiState.value = _uiState.value.copy(isLoading = false, hasError = true)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LocationsViewModel(appContainer().locationRepository)
            }
        }
    }
}
