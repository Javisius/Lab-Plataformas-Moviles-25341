package com.example.lab7rickymorty

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val innerNavController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        innerNavController.navigate(CharactersGraph) {
                            popUpTo(innerNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Face, contentDescription = null) },
                    label = { Text("Characters") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        innerNavController.navigate(LocationsGraph) {
                            popUpTo(innerNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Place, contentDescription = null) },
                    label = { Text("Locations") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                        innerNavController.navigate(Profile) {
                            popUpTo(innerNavController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = innerNavController,
            startDestination = CharactersGraph,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Grafo anidado de Characters
            navigation<CharactersGraph>(startDestination = CharactersList) {
                composable<CharactersList> {
                    CharactersScreen(
                        onCharacterClick = { id -> innerNavController.navigate(CharacterDetail(id)) }
                    )
                }
                composable<CharacterDetail> {
                    // Se saca solo el CharacterDetail de la pila: si se llama dos veces,
                    // la segunda no encuentra el destino y no hace nada (no saca la lista)
                    CharacterDetailScreen(
                        onBackClick = { innerNavController.popBackStack<CharacterDetail>(inclusive = true) }
                    )
                }
            }

            // Grafo anidado de Locations
            navigation<LocationsGraph>(startDestination = LocationsList) {
                composable<LocationsList> {
                    LocationsScreen(
                        onLocationClick = { id -> innerNavController.navigate(LocationDetail(id)) }
                    )
                }
                composable<LocationDetail> {
                    // Se saca solo el LocationDetail de la pila: si se llama dos veces,
                    // la segunda no encuentra el destino y no hace nada (no saca la lista)
                    LocationDetailScreen(
                        onBackClick = { innerNavController.popBackStack<LocationDetail>(inclusive = true) }
                    )
                }
            }

            // Profile es una sola pantalla, sin grafo propio
            composable<Profile> {
                ProfileScreen(onLogoutClick = onLogout)
            }
        }
    }
}