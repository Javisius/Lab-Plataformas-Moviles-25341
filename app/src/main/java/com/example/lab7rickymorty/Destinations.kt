package com.example.lab7rickymorty

import kotlinx.serialization.Serializable

// Nivel raíz: lo que maneja el NavHost de afuera
@Serializable
object Login

@Serializable
object Main

// Grafo anidado de Characters (vive dentro de Main)
@Serializable
object CharactersGraph

@Serializable
object CharactersList

@Serializable
data class CharacterDetail(val id: Int)

// Grafo anidado de Locations (vive dentro de Main)
@Serializable
object LocationsGraph

@Serializable
object LocationsList

@Serializable
data class LocationDetail(val id: Int)

// Profile no necesita grafo propio, es una sola pantalla
@Serializable
object Profile