package com.example.lab7rickymorty

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Login) {

        composable<Login> {
            LoginScreen(
                onStartClick = {
                    navController.navigate(Main) {
                        popUpTo<Login> { inclusive = true }
                    }
                }
            )
        }

        composable<Main> {
            MainScreen(
                onLogout = {
                    navController.navigate(Login) {
                        popUpTo<Main> { inclusive = true }
                    }
                }
            )
        }
    }
}