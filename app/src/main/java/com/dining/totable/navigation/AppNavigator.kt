package com.dining.totable.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dining.totable.ui.screens.HomeScreen
import com.dining.totable.ui.screens.LoginScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    NavHost(
        navController = navController,
        startDestination = if (auth.currentUser != null) "home" else "login"
    ) {
        composable("login") { LoginScreen(navController) }
        composable("home") { HomeScreen(navController) }
    }
}