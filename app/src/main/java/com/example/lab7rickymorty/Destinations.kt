package com.example.lab7rickymorty

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Characters

@Serializable
data class CharacterDetail(val id: Int)