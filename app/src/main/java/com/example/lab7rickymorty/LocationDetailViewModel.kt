package com.example.lab7rickymorty

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val locationId: Int = savedStateHandle.toRoute<LocationDetail>().id
    private val locationDao = AppDatabase.getDatabase(application).locationDao()

    private val _uiState = MutableStateFlow(UiState<Location>())
    val uiState: StateFlow<UiState<Location>> = _uiState

    init {
        loadLocation()
    }

    fun loadLocation() {
        viewModelScope.launch {
            _uiState.value = UiState(isLoading = true)
            val entity = locationDao.getById(locationId)
            _uiState.value = UiState(isLoading = false, data = entity.toModel())
        }
    }

    fun setError() {
        _uiState.value = _uiState.value.copy(isLoading = false, hasError = true)
    }
}