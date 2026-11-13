package com.example.lab7rickymorty.data.remote

import retrofit2.http.GET
import retrofit2.http.Url

interface RickAndMortyApiService {

    // Primera página (relativa a la base URL)
    @GET("character")
    suspend fun getCharacters(): PageResponseDto<CharacterDto>

    // Páginas siguientes: recibe la URL completa que viene en info.next
    @GET
    suspend fun getCharactersPage(@Url url: String): PageResponseDto<CharacterDto>

    @GET("location")
    suspend fun getLocations(): PageResponseDto<LocationDto>

    @GET
    suspend fun getLocationsPage(@Url url: String): PageResponseDto<LocationDto>
}
