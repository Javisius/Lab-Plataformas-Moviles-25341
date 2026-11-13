package com.example.lab7rickymorty

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Único punto de acceso a los personajes: decide de dónde salen los datos.
// La UI siempre lee de Room; el API solo se usa para llenar Room en el login.
class CharacterRepository(
    private val apiService: RickAndMortyApiService,
    private val characterDao: CharacterDao
) {

    fun getCharacters(): Flow<List<CharacterListItem>> {
        return characterDao.getAll().map { entities -> entities.map { it.toListItem() } }
    }

    fun getCharacterById(id: Int): Flow<CharacterListItem?> {
        return characterDao.getById(id).map { entity -> entity?.toListItem() }
    }

    suspend fun setFavorite(id: Int, isFavorite: Boolean) {
        characterDao.updateFavorite(id, isFavorite)
    }

    // Descarga TODAS las páginas siguiendo info.next y las guarda en Room,
    // solo si la tabla está vacía (así nunca se duplica ni se pisan cambios locales)
    suspend fun syncIfEmpty() {
        if (characterDao.count() > 0) return

        val allCharacters = mutableListOf<CharacterDto>()
        var page = apiService.getCharacters()
        allCharacters += page.results

        var nextUrl = page.info.next
        while (nextUrl != null) {
            page = apiService.getCharactersPage(nextUrl)
            allCharacters += page.results
            nextUrl = page.info.next
        }

        // Se inserta todo al final: si falla una página a la mitad, no queda nada a medias
        characterDao.insertAll(allCharacters.map { it.toEntity() })
    }

    suspend fun clearAll() {
        characterDao.deleteAll()
    }
}
