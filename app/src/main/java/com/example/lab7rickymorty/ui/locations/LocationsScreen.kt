package com.example.lab7rickymorty.ui.locations

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import com.example.lab7rickymorty.model.LocationListItem
import com.example.lab7rickymorty.ui.common.EmptyContent
import com.example.lab7rickymorty.ui.common.ErrorContent
import com.example.lab7rickymorty.ui.common.LoadingContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreen(
    onLocationClick: (Int) -> Unit,
    viewModel: LocationsViewModel = viewModel(factory = LocationsViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val filter by viewModel.filter.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Locations") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        when {
            uiState.hasError -> ErrorContent(
                modifier = Modifier.padding(innerPadding),
                onRetryClick = { viewModel.loadLocations() }
            )

            uiState.isLoading -> LoadingContent(
                modifier = Modifier.padding(innerPadding),
                onClick = { viewModel.setError() }
            )

            else -> {
                val items = uiState.data ?: emptyList()
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    LocationFilterRow(
                        selected = filter,
                        onSelected = { viewModel.setFilter(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    if (items.isEmpty()) {
                        EmptyContent(
                            message = when (filter) {
                                LocationFilter.ALL -> "No hay locations para mostrar"
                                LocationFilter.FAVORITES -> "No hay locations favoritas"
                                LocationFilter.NON_FAVORITES -> "No hay locations sin marcar como favoritas"
                            }
                        )
                    } else {
                        LazyColumn {
                            items(items, key = { it.location.id }) { item ->
                                LocationRow(
                                    item = item,
                                    onClick = { onLocationClick(item.location.id) },
                                    onFavoriteClick = {
                                        viewModel.toggleFavorite(item.location.id, item.isFavorite)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationFilterRow(
    selected: LocationFilter,
    onSelected: (LocationFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = LocationFilter.entries
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option == selected,
                onClick = { onSelected(option) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size)
            ) {
                Text(option.label)
            }
        }
    }
}

@Composable
private fun LocationRow(
    item: LocationListItem,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 24.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.location.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = item.location.type,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        IconButton(onClick = onFavoriteClick) {
            Icon(
                imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Favorito",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
