package com.dining.totable

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.dining.totable.navigation.AppNavigator
import com.dining.totable.ui.theme.ToTableTheme
import com.dining.totable.utils.DeviceConfigManager
import com.dining.totable.utils.PermissionHelper
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {

    private lateinit var deviceConfigManager: DeviceConfigManager
    private var currentTopic: String? = null
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        deviceConfigManager = DeviceConfigManager(this)
        sharedPreferences = getSharedPreferences("device_config", MODE_PRIVATE)

        setContent {
            ToTableTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Request the notification permission
                    PermissionHelper.RequestNotificationPermission(
                        onPermissionGranted = {
                            // Permission granted
                        },
                        onPermissionDenied = {
                            // Handle the case where permission is denied
                        }
                    )
                    AppNavigator()
                }
            }
        }
        handleConfigurationChange()
        sharedPreferences.registerOnSharedPreferenceChangeListener { _, key ->
            if (key == "config") {
                handleConfigurationChange()
            }
        }
    }

    private fun handleConfigurationChange() {
        val config = deviceConfigManager.getConfiguration()
        if (config == null) {
            Log.d("FCM", "No configuration available, skipping topic subscription")
            return
        }

        val restaurantId = config.restaurantId
        val roleId = config.deviceRoleId

        if (restaurantId.isNotEmpty() && !roleId.isNullOrEmpty()) {
            val newTopic = "restaurant_${restaurantId}_role_${roleId}"
            Log.d("FCM", "New topic to subscribe: $newTopic")

            currentTopic?.let { oldTopic ->
                FirebaseMessaging.getInstance().unsubscribeFromTopic(oldTopic)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("FCM", "Unsubscribed from old topic: $oldTopic")
                        } else {
                            Log.e("FCM", "Failed to unsubscribe from old topic: $oldTopic", task.exception)
                        }
                    }
            }

            FirebaseMessaging.getInstance().subscribeToTopic(newTopic)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FCM", "Subscribed to topic: $newTopic")
                        currentTopic = newTopic
                    } else {
                        Log.e("FCM", "Subscription failed for topic: $newTopic", task.exception)
                    }
                }
        } else {
            Log.d("FCM", "restaurantId or roleId is missing: restaurantId=$restaurantId, roleId=$roleId")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sharedPreferences.unregisterOnSharedPreferenceChangeListener { _, _ -> }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ToTableTheme {
        Greeting("Android")
    }
}