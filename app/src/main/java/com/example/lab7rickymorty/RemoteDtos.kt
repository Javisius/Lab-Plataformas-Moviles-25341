package com.example.lab7rickymorty

import kotlinx.serialization.Serializable

// DTOs: reflejan exactamente el JSON del API. Solo declaramos los campos que usamos;
// el resto (origin, episode, url, created...) se ignora gracias a ignoreUnknownKeys.

// Respuesta paginada del API: { "info": {...}, "results": [...] }
@Serializable
data class PageResponseDto<T>(
    val info: InfoDto,
    val results: List<T>
)

// info.next es la URL de la siguiente página, o null en la última
@Serializable
data class InfoDto(
    val count: Int,
    val pages: Int,
    val next: String? = null,
    val prev: String? = null
)

@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val gender: String,
    val image: String
)

@Serializable
data class LocationDto(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String
)
