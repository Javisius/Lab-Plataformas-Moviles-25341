package com.example.lab7rickymorty

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

fun Location.toEntity(): LocationEntity {
    return LocationEntity(
        id = id,
        name = name,
        type = type,
        dimension = dimension
    )
}

data class CharacterListItem(
    val character: Character,
    val isFavorite: Boolean
)

fun CharacterEntity.toListItem(): CharacterListItem {
    return CharacterListItem(character = toModel(), isFavorite = isFavorite)
}