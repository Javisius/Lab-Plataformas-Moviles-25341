package com.example.lab7rickymorty.data.repository

import com.example.lab7rickymorty.data.local.LocationDao
import com.example.lab7rickymorty.data.mapper.toEntity
import com.example.lab7rickymorty.data.mapper.toListItem
import com.example.lab7rickymorty.data.remote.LocationDto
import com.example.lab7rickymorty.data.remote.RickAndMortyApiService
import com.example.lab7rickymorty.model.LocationListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationRepository(
    private val apiService: RickAndMortyApiService,
    private val locationDao: LocationDao
) {

    fun getLocations(): Flow<List<LocationListItem>> {
        return locationDao.getAll().map { entities -> entities.map { it.toListItem() } }
    }

    fun getLocationById(id: Int): Flow<LocationListItem?> {
        return locationDao.getById(id).map { entity -> entity?.toListItem() }
    }

    suspend fun setFavorite(id: Int, isFavorite: Boolean) {
        locationDao.updateFavorite(id, isFavorite)
    }

    // Igual que en CharacterRepository: todas las páginas, solo si la tabla está vacía
    suspend fun syncIfEmpty() {
        if (locationDao.count() > 0) return

        val allLocations = mutableListOf<LocationDto>()
        var page = apiService.getLocations()
        allLocations += page.results

        var nextUrl = page.info.next
        while (nextUrl != null) {
            page = apiService.getLocationsPage(nextUrl)
            allLocations += page.results
            nextUrl = page.info.next
        }

        locationDao.insertAll(allLocations.map { it.toEntity() })
    }

    suspend fun clearAll() {
        locationDao.deleteAll()
    }
}
