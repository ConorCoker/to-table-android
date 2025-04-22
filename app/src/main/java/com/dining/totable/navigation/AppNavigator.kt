package com.dining.totable.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dining.totable.ui.screens.LoginScreen
import com.dining.totable.ui.screens.MainScreen
import com.dining.totable.ui.screens.RoleSelectionScreen
import com.dining.totable.utils.DeviceConfigManager
import com.dining.totable.viewmodels.ToTableViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val viewModel: ToTableViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = if (auth.currentUser != null) {
            if (DeviceConfigManager(LocalContext.current).getConfiguration()!!.deviceRoleId != null) {
                "home"
            } else "role_selection"
        } else {
            "login"
        }
    ) {
        composable("login") { LoginScreen(navController, viewModel) }
        composable("home") { MainScreen(navController,viewModel) }
        composable(route = "role_selection") { RoleSelectionScreen(navController, viewModel) }
    }
}