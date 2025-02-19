package com.dining.totable.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.dining.totable.R
import com.dining.totable.ui.composables.buttons.ToTableButton
import com.dining.totable.ui.composables.menus.RoleDropdown
import com.dining.totable.ui.composables.textfields.ToTableTextField
import com.dining.totable.utils.DeviceConfigManager
import com.dining.totable.utils.DeviceConfiguration
import com.dining.totable.utils.DeviceRole
import com.dining.totable.utils.RestaurantIdToValidEmailConverter.toValidFirebaseAuthEmail
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController) {
    var restaurantId by remember { mutableStateOf("") }
    var restaurantPassword by remember { mutableStateOf("") }
    var role: DeviceRole? by remember { mutableStateOf(null) }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ToTableTextField(
            value = restaurantId,
            onValueChange = { restaurantId = it },
            label = { Text(stringResource(R.string.restaurant_id)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.storefront_24px),
                    contentDescription = "Storefront Icon"
                )
            },
            keyboardOptions = KeyboardOptions(), // change this in future depending on how ids are generated
            isError = false
        )
        ToTableTextField(
            value = restaurantPassword,
            onValueChange = { restaurantPassword = it },
            label = { Text(stringResource(R.string.password)) },
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.password_24px),
                    contentDescription = "Storefront Icon"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            isError = false
        )
        RoleDropdown(selectedRole = role, onRoleSelected = { selectedRole ->
            role = selectedRole
        })
        val context = LocalContext.current
        ToTableButton(stringResource(R.string.login)) {
            if (role != null) { // ensure they select a role before attempting login
                login(
                    restaurantId = restaurantId,
                    restaurantPassword = restaurantPassword,
                    onLoginAttemptFailed = {
                        Log.w(TAG, "$it ?: ${context.getString(R.string.login_failed)}")
                        Toast.makeText(
                            context,
                            context.getString(R.string.login_failed),
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
                    onLoginAttemptSuccess = {
                        DeviceConfigManager(context).saveConfiguration(
                            DeviceConfiguration(
                                deviceRole = role!! // save the role so device starts in correct role next time
                            )
                        )
                        navController.navigate("home")
                    }
                )
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.role_unselected),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}

private fun login(
    restaurantId: String,
    restaurantPassword: String,
    onLoginAttemptFailed: (String?) -> Unit,
    onLoginAttemptSuccess: () -> Unit
) {
    FirebaseAuth.getInstance()
        .signInWithEmailAndPassword(restaurantId.toValidFirebaseAuthEmail(), restaurantPassword)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onLoginAttemptSuccess()
            } else {
                onLoginAttemptFailed(task.exception?.localizedMessage)
            }
        }
}

const val TAG = "LoginScreen"

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun LoginScreenPreview() {
//    LoginScreen()
}