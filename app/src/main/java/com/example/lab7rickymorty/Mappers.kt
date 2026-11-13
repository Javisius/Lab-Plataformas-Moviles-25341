package com.example.lab7rickymorty

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

data class CharacterListItem(
    val character: Character,
    val isFavorite: Boolean
)

fun CharacterEntity.toListItem(): CharacterListItem {
    return CharacterListItem(character = toModel(), isFavorite = isFavorite)
}

data class LocationListItem(
    val location: Location,
    val isFavorite: Boolean
)

fun LocationEntity.toListItem(): LocationListItem {
    return LocationListItem(location = toModel(), isFavorite = isFavorite)
}