package com.example.lab7rickymorty.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface RickAndMortyApiService {

    // Primera página (relativa a la base URL)
    @GET("character")
    suspend fun getCharacters(): PageResponseDto<CharacterDto>

    // Páginas siguientes: recibe la URL completa que viene en info.next
    @GET
    suspend fun getCharactersPage(@Url url: String): PageResponseDto<CharacterDto>

    // Varios personajes en UN request: /character/1,2,3 devuelve un arreglo JSON (no una página).
    // encoded = true: las comas se envían literales en vez de convertirse en %2C
    @GET("character/{ids}")
    suspend fun getCharactersByIds(@Path("ids", encoded = true) ids: String): List<CharacterDto>

    @GET("location")
    suspend fun getLocations(): PageResponseDto<LocationDto>

    @GET
    suspend fun getLocationsPage(@Url url: String): PageResponseDto<LocationDto>

    @GET("location/{ids}")
    suspend fun getLocationsByIds(@Path("ids", encoded = true) ids: String): List<LocationDto>
}
