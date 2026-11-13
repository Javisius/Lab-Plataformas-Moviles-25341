package com.example.lab7rickymorty.data.mapper

import com.example.lab7rickymorty.data.local.CharacterEntity
import com.example.lab7rickymorty.data.local.LocationEntity
import com.example.lab7rickymorty.data.remote.CharacterDto
import com.example.lab7rickymorty.data.remote.LocationDto
import com.example.lab7rickymorty.model.Character
import com.example.lab7rickymorty.model.CharacterListItem
import com.example.lab7rickymorty.model.Location
import com.example.lab7rickymorty.model.LocationListItem

// DTO (API) -> Entity (Room)
fun CharacterDto.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        gender = gender,
        image = image
    )
}

fun LocationDto.toEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        name = name,
        type = type,
        dimension = dimension
    )
}

// Entity (Room) -> Model (UI)
fun CharacterEntity.toModel(): Character {
    return Character(
        id = id,
        name = name,
        status = status,
        species = species,
        gender = gender,
        image = image
    )
}

fun LocationEntity.toModel(): Location {
    return Location(
        id = id,
        name = name,
        type = type,
        dimension = dimension
    )
}

fun Character.toEntity(): CharacterEntity {
    return CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        gender = gender,
        image = image
    )
}

fun CharacterEntity.toListItem(): CharacterListItem {
    return CharacterListItem(character = toModel(), isFavorite = isFavorite)
}

fun LocationEntity.toListItem(): LocationListItem {
    return LocationListItem(location = toModel(), isFavorite = isFavorite)
}
