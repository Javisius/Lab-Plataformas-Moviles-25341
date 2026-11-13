package com.example.lab7rickymorty.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.lab7rickymorty.data.local.UserPreferences
import com.example.lab7rickymorty.data.repository.CharacterRepository
import com.example.lab7rickymorty.data.repository.LocationRepository
import com.example.lab7rickymorty.di.appContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userPreferences: UserPreferences,
    private val characterRepository: CharacterRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    val name: StateFlow<String?> = userPreferences.getName()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clearName()
            characterRepository.clearAll()
            locationRepository.clearAll()
            onDone()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                ProfileViewModel(
                    userPreferences = container.userPreferences,
                    characterRepository = container.characterRepository,
                    locationRepository = container.locationRepository
                )
            }
        }
    }
}
