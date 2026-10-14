package com.example.lab7rickymorty

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    fun saveNameAndContinue(name: String, onDone: () -> Unit) {
        viewModelScope.launch {
            userPreferences.saveName(name)
            onDone()
        }
    }
}