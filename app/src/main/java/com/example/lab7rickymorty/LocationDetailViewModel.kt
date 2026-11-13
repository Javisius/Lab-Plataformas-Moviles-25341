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

class LocationDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val locationId: Int = savedStateHandle.toRoute<LocationDetail>().id

    private val _uiState = MutableStateFlow(UiState<Location>())
    val uiState: StateFlow<UiState<Location>> = _uiState.asStateFlow()

    // Las locations no se eliminan, pero si la fila no existe (p. ej. tras un logout)
    // se vuelve atrás en lugar de quedarse cargando o crashear
    private val _notFound = MutableStateFlow(false)
    val notFound: StateFlow<Boolean> = _notFound.asStateFlow()

    private var observeJob: Job? = null

    init {
        loadLocation()
    }

    fun loadLocation() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            locationRepository.getLocationById(locationId).collect { location ->
                if (location == null) {
                    _uiState.value = UiState(isLoading = false)
                    _notFound.value = true
                } else {
                    _uiState.value = UiState(isLoading = false, data = location)
                }
            }
        }
    }

    fun setError() {
        observeJob?.cancel()
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
