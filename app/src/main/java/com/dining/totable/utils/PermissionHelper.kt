package com.dining.totable.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

object PermissionHelper {

    private fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permissions are automatically granted on versions below Android 13
        }
    }

    @Composable
    fun RequestNotificationPermission(
        onPermissionGranted: () -> Unit,
        onPermissionDenied: () -> Unit
    ) {
        // Initialize the ActivityResultLauncher to request permissions
        // `rememberLauncherForActivityResult` creates and remembers a launcher for requesting permissions.
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission() // The contract for requesting a single permission
        ) { isGranted ->  // This block is called when the permission request completes
            if (isGranted) {
                onPermissionGranted()
            } else {
                onPermissionDenied()
            }
        }

        // LocalContext.current is a way to access the context inside composables
        // Context is required to check permissions and use system features
        val context = LocalContext.current

        // `remember` is used to store values that persist across recompositions.
        // `mutableStateOf` is used to create a state that tracks whether the permission is granted or not.
        val hasPermission = remember { mutableStateOf(hasNotificationPermission(context)) }

        // LaunchedEffect is a composable that triggers side effects (like launching a permission request)
        // `LaunchedEffect(Unit)` ensures this block is only run once when the composable is first launched.
        LaunchedEffect(Unit) {
            // If the device is running Android 13 (Tiramisu) or higher, and the permission is not granted yet
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasPermission.value) {
                // Launch the permission request if the permission is not granted
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

}


