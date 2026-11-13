package com.example.lab7rickymorty.data.repository

import com.example.lab7rickymorty.data.local.CharacterDao
import com.example.lab7rickymorty.data.mapper.toEntity
import com.example.lab7rickymorty.data.mapper.toListItem
import com.example.lab7rickymorty.data.remote.RickAndMortyApiService
import com.example.lab7rickymorty.model.Character
import com.example.lab7rickymorty.model.CharacterListItem
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

    // CRUD local: el API es de solo lectura, todos estos cambios existen solo en Room

    // El id que trae el Character se ignora: se genera uno que no choque con los del API
    suspend fun createCharacter(character: Character): Int {
        val maxId = characterDao.getMaxId() ?: 0
        val newId = maxOf(maxId, LOCAL_ID_START) + 1
        characterDao.insert(character.copy(id = newId).toEntity())
        return newId
    }

    suspend fun updateCharacter(character: Character) {
        characterDao.updateData(
            id = character.id,
            name = character.name,
            status = character.status,
            species = character.species,
            gender = character.gender,
            image = character.image
        )
    }

    // Sirve igual para personajes creados localmente y para los que vinieron del API
    suspend fun deleteCharacter(id: Int) {
        characterDao.deleteById(id)
    }

    // Descarga TODOS los personajes (ver fetchAllRecords: página 1 + un request por ids,
    // o páginas como respaldo) y los guarda en Room, solo si la tabla está vacía
    // (así nunca se duplica ni se pisan cambios locales)
    suspend fun syncIfEmpty() {
        if (characterDao.count() > 0) return

        val allCharacters = fetchAllRecords(
            resource = "character",
            firstPage = { apiService.getCharacters() },
            byIds = { ids -> apiService.getCharactersByIds(ids) },
            nextPage = { url -> apiService.getCharactersPage(url) }
        )

        // Se inserta todo al final: si falla una petición a la mitad, no queda nada a medias
        characterDao.insertAll(allCharacters.map { it.toEntity() })
    }

    suspend fun clearAll() {
        characterDao.deleteAll()
    }

    companion object {
        // Los ids del API van de 1 a ~826. Los personajes creados en la app empiezan
        // después de 1_000_000, así nunca chocan con un id del API
        const val LOCAL_ID_START = 1_000_000
    }
}
