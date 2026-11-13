package com.example.lab7rickymorty.ui.characters

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab7rickymorty.ui.common.CharacterImage
import com.example.lab7rickymorty.ui.common.EmptyContent
import com.example.lab7rickymorty.ui.common.ErrorContent
import com.example.lab7rickymorty.ui.common.LoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    onCharacterClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    viewModel: CharactersViewModel = viewModel(factory = CharactersViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val showOnlyFavorites by viewModel.showOnlyFavorites.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Characters") },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavoritesFilter() }) {
                        Icon(
                            imageVector = if (showOnlyFavorites) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Filtrar favoritos",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Crear personaje")
            }
        }
    ) { innerPadding ->
        when {
            uiState.hasError -> ErrorContent(
                modifier = Modifier.padding(innerPadding),
                onRetryClick = { viewModel.loadCharacters() }
            )

            uiState.isLoading -> LoadingContent(
                modifier = Modifier.padding(innerPadding),
                onClick = { viewModel.setError() }
            )

            else -> {
                val items = uiState.data ?: emptyList()
                if (items.isEmpty()) {
                    EmptyContent(
                        message = if (showOnlyFavorites) "No hay favoritos para mostrar" else "No hay personajes para mostrar",
                        modifier = Modifier.padding(innerPadding)
                    )
                } else {
                    LazyColumn(modifier = Modifier.padding(innerPadding)) {
                        items(items, key = { it.character.id }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCharacterClick(item.character.id) }
                                    .padding(horizontal = 24.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CharacterImage(url = item.character.image, size = 64.dp)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.character.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "${item.character.species} - ${item.character.status}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.toggleFavorite(item.character.id, item.isFavorite)
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                        contentDescription = "Favorito",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
