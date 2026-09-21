package com.example.lab7rickymorty

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Login) {

        composable<Login> {
            LoginScreen(
                onStartClick = {
                    navController.navigate(Characters) {
                        popUpTo<Login> { inclusive = true }
                    }
                }
            )
        }

        composable<Characters> {
            CharactersScreen(
                onCharacterClick = { id ->
                    navController.navigate(CharacterDetail(id))
                }
            )
        }

        composable<CharacterDetail> { backStackEntry ->
            val args = backStackEntry.toRoute<CharacterDetail>()
            CharacterDetailScreen(
                id = args.id,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}