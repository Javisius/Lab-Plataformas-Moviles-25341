package com.example.lab7rickymorty

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Splash) {

        composable<Splash> {
            SplashScreen(
                onLoggedIn = {
                    navController.navigate(Main) {
                        popUpTo<Splash> { inclusive = true }
                    }
                },
                onLoggedOut = {
                    navController.navigate(Login) {
                        popUpTo<Splash> { inclusive = true }
                    }
                }
            )
        }

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