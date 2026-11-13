package com.example.lab7rickymorty

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    // ignoreUnknownKeys: el JSON trae más campos de los que declaramos en los DTOs
    private val json = Json { ignoreUnknownKeys = true }

    // Timeouts explícitos para que, sin internet o con red lenta, el login falle
    // en un tiempo razonable y podamos mostrar el error
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    val apiService: RickAndMortyApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(RickAndMortyApiService::class.java)
    }
}
