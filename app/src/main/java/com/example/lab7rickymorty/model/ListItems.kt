package com.example.lab7rickymorty.model

// Modelos para la UI: el dato + su estado de favorito (que solo existe en Room)
data class CharacterListItem(
    val character: Character,
    val isFavorite: Boolean
)

data class LocationListItem(
    val location: Location,
    val isFavorite: Boolean
)
