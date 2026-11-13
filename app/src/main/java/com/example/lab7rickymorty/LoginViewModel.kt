package com.example.lab7rickymorty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userPreferences: UserPreferences,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun saveNameAndContinue(name: String, onDone: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            userPreferences.saveName(name)
            characterRepository.syncIfEmpty()
            locationRepository.syncIfEmpty()

            _isLoading.value = false
            onDone()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                LoginViewModel(
                    userPreferences = container.userPreferences,
                    characterRepository = container.characterRepository,
                    locationRepository = container.locationRepository
                )
            }
        }
    }
}
