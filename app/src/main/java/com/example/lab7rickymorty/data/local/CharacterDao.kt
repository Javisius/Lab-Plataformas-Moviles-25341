package com.example.lab7rickymorty.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(characters: List<CharacterEntity>): List<Long>

    @Query("SELECT * FROM characters")
    fun getAll(): Flow<List<CharacterEntity>>

    // Flow + nullable: Room vuelve a emitir cada vez que cambia la fila,
    // y emite null si la fila ya no existe (por ejemplo, si se eliminó)
    @Query("SELECT * FROM characters WHERE id = :id")
    fun getById(id: Int): Flow<CharacterEntity?>

    @Query("SELECT * FROM characters WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<CharacterEntity>>

    @Query("UPDATE characters SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean): Int

    // ABORT: un personaje nuevo nunca debe reemplazar a uno existente
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(character: CharacterEntity): Long

    // UPDATE de solo los campos editables: isFavorite se conserva tal cual
    @Query(
        "UPDATE characters SET name = :name, status = :status, species = :species, " +
            "gender = :gender, image = :image WHERE id = :id"
    )
    suspend fun updateData(
        id: Int,
        name: String,
        status: String,
        species: String,
        gender: String,
        image: String
    ): Int

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    // null si la tabla está vacía
    @Query("SELECT MAX(id) FROM characters")
    suspend fun getMaxId(): Int?

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun count(): Int

    @Query("DELETE FROM characters")
    suspend fun deleteAll(): Int
}