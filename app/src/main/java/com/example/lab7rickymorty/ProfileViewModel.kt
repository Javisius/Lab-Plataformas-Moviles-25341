package com.example.lab7rickymorty

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val database = AppDatabase.getDatabase(application)
    private val characterDao = database.characterDao()
    private val locationDao = database.locationDao()

    val name: StateFlow<String?> = userPreferences.getName()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clearName()
            characterDao.deleteAll()
            locationDao.deleteAll()
            onDone()
        }
    }
}