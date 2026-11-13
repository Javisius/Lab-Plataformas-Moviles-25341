package com.example.lab7rickymorty

import android.content.Context

// Inyección de dependencias manual: aquí se crean UNA sola vez las dependencias
// de la app y se conectan entre sí. Los ViewModels las piden al contenedor.
class AppContainer(context: Context) {

    private val database = AppDatabase.getDatabase(context)
    private val apiService = RetrofitClient.apiService

    val userPreferences = UserPreferences(context)

    val characterRepository = CharacterRepository(apiService, database.characterDao())

    val locationRepository = LocationRepository(apiService, database.locationDao())
}
