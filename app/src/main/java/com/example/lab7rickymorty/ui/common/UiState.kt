package com.example.lab7rickymorty.ui.common

data class UiState<T>(
    val isLoading: Boolean = true,
    val data: T? = null,
    val hasError: Boolean = false
)