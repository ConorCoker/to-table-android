package com.dining.totable.ui.screens

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.dining.totable.ui.composables.MainScreenTopBar
import com.dining.totable.ui.utils.TopBarOption
import com.dining.totable.utils.DeviceConfigManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun MainScreen(navController: NavController) {
    val activity = LocalActivity.current
    val context = LocalContext.current
    DisposableEffect(Unit) {
        // Force landscape when entering the screen
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            // Reset to system default when leaving the screen
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        MainScreenTopBar(navController = navController,
            onDropdownMenuItemClicked = { dropdownMenuItemClicked ->
                if (dropdownMenuItemClicked == TopBarOption.Logout) {
                    DeviceConfigManager(context).logout(navController)
                } else {
                    //dialog for choose role and new screen for settings
                }
            }
        )
    }) { paddingValues ->
        val todo = paddingValues
    }
}