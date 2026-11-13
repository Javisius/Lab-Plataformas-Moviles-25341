package com.example.lab7rickymorty

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras

// Se crea antes que cualquier Activity y vive mientras viva el proceso,
// por eso es el lugar ideal para guardar el contenedor de dependencias
class RickMortyApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

// Atajo para los initializer { } de las factories de ViewModels
fun CreationExtras.appContainer(): AppContainer {
    return (this[APPLICATION_KEY] as RickMortyApplication).container
}
