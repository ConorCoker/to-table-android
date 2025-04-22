package com.dining.totable.utils

import android.content.Context
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

data class DeviceConfiguration(
    val deviceRoleName: String? = null,
    val deviceRoleId: String? = null,
    val restaurantEmail: String,
    val restaurantId: String
)

class DeviceConfigManager(context: Context) {

    private val PREFS_NAME = "device_config"
    private val KEY_CONFIG = "config"

    private val sharedPreferences =
        context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

    private val gson = Gson()

    fun saveConfiguration(
        configuration: DeviceConfiguration
    ) {
        val json = gson.toJson(configuration) // converting DeviceConfiguration to json
        sharedPreferences.edit().putString(KEY_CONFIG, json).apply()
    }

    fun getConfiguration(): DeviceConfiguration? {
        val jsonString = sharedPreferences.getString(KEY_CONFIG, null)
        return if (jsonString != null) {
            gson.fromJson(jsonString, DeviceConfiguration::class.java)
        } else null
    }

    fun logout(navController: NavController) {
        Firebase.auth.signOut()
        sharedPreferences.edit().remove(KEY_CONFIG).apply()
        navController.navigate("login")
    }
}