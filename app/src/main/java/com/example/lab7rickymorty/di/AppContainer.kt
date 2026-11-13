package com.example.lab7rickymorty.di

import android.content.Context
import com.example.lab7rickymorty.data.local.AppDatabase
import com.example.lab7rickymorty.data.local.UserPreferences
import com.example.lab7rickymorty.data.remote.RetrofitClient
import com.example.lab7rickymorty.data.repository.CharacterRepository
import com.example.lab7rickymorty.data.repository.LocationRepository

// Inyección de dependencias manual: aquí se crean UNA sola vez las dependencias
// de la app y se conectan entre sí. Los ViewModels las piden al contenedor.
class AppContainer(context: Context) {

    private val database = AppDatabase.getDatabase(context)
    private val apiService = RetrofitClient.apiService

    val userPreferences = UserPreferences(context)

    val characterRepository = CharacterRepository(apiService, database.characterDao())

    val locationRepository = LocationRepository(apiService, database.locationDao())
}
