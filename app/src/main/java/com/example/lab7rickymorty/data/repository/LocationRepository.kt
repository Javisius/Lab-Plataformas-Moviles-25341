package com.example.lab7rickymorty.data.repository

import com.example.lab7rickymorty.data.local.LocationDao
import com.example.lab7rickymorty.data.mapper.toEntity
import com.example.lab7rickymorty.data.mapper.toListItem
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

    // Igual que en CharacterRepository: todos los registros, solo si la tabla está vacía
    suspend fun syncIfEmpty() {
        if (locationDao.count() > 0) return

        val allLocations = fetchAllRecords(
            resource = "location",
            firstPage = { apiService.getLocations() },
            byIds = { ids -> apiService.getLocationsByIds(ids) },
            nextPage = { url -> apiService.getLocationsPage(url) }
        )

        locationDao.insertAll(allLocations.map { it.toEntity() })
    }

    suspend fun clearAll() {
        locationDao.deleteAll()
    }
}
