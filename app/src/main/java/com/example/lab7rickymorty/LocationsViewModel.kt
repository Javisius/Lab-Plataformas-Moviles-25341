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

// Las 3 opciones del filtro de locations
enum class LocationFilter(val label: String) {
    ALL("Todas"),
    FAVORITES("Favoritas"),
    NON_FAVORITES("No favoritas")
}

class LocationsViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState<List<LocationListItem>>())
    val uiState: StateFlow<UiState<List<LocationListItem>>> = _uiState.asStateFlow()

    private val _filter = MutableStateFlow(LocationFilter.ALL)
    val filter: StateFlow<LocationFilter> = _filter.asStateFlow()

    private var observeJob: Job? = null

    init {
        loadLocations()
    }

    fun loadLocations() {
        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)

            // combine vuelve a calcular la lista cuando cambia Room O cuando cambia el filtro
            combine(
                locationRepository.getLocations(),
                _filter
            ) { items, filter ->
                when (filter) {
                    LocationFilter.ALL -> items
                    LocationFilter.FAVORITES -> items.filter { it.isFavorite }
                    LocationFilter.NON_FAVORITES -> items.filter { !it.isFavorite }
                }
            }.collect { filteredItems ->
                _uiState.value = UiState(isLoading = false, data = filteredItems)
            }
        }
    }

    fun setFilter(filter: LocationFilter) {
        _filter.value = filter
    }

    fun toggleFavorite(id: Int, currentValue: Boolean) {
        viewModelScope.launch {
            locationRepository.setFavorite(id, !currentValue)
        }
    }

    fun setError() {
        observeJob?.cancel()
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
