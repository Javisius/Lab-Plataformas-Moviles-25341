package com.example.lab7rickymorty

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val database = AppDatabase.getDatabase(application)
    private val characterDao = database.characterDao()
    private val locationDao = database.locationDao()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun saveNameAndContinue(name: String, onDone: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            userPreferences.saveName(name)
            syncIfNeeded()

            _isLoading.value = false
            onDone()
        }
    }

    private suspend fun syncIfNeeded() {
        val charactersCount = characterDao.count()
        val locationsCount = locationDao.count()

        if (charactersCount == 0 && locationsCount == 0) {
            delay(4000)

            val characterEntities = CharacterDb().getAllCharacters().map { it.toEntity() }
            val locationEntities = LocationDb().getAllLocations().map { it.toEntity() }

            characterDao.insertAll(characterEntities)
            locationDao.insertAll(locationEntities)
        }
    }
}