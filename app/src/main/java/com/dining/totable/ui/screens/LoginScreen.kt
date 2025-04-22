package com.dining.totable.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dining.totable.R
import com.dining.totable.ui.composables.buttons.ToTableButton
import com.dining.totable.ui.composables.textfields.ToTableTextField
import com.dining.totable.utils.DeviceConfigManager
import com.dining.totable.utils.DeviceConfiguration
import com.dining.totable.viewmodels.ToTableViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: ToTableViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var restaurantEmail by remember { mutableStateOf("") }
    var restaurantPassword by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ToTableTextField(
            value = restaurantEmail,
            onValueChange = { restaurantEmail = it },
            label = { Text(stringResource(R.string.restaurant_email)) },
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
        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
        }
        ToTableButton(stringResource(R.string.login)) {
                coroutineScope.launch {
                    isLoading = true
                    errorMessage = null
                    login(
                        restaurantEmail = restaurantEmail,
                        restaurantPassword = restaurantPassword,
                        viewModel = viewModel,
                        onLoginAttemptFailed = {
                            Log.w(TAG, "$it ?: ${context.getString(R.string.login_failed)}")
                            Toast.makeText(
                                context,
                                context.getString(R.string.login_failed),
                                Toast.LENGTH_SHORT,
                            ).show()
                            isLoading = false
                        },
                        onLoginAttemptSuccess = { restaurantId ->
                            DeviceConfigManager(context).saveConfiguration(
                                DeviceConfiguration(
                                    restaurantEmail = restaurantEmail,
                                    restaurantId = restaurantId
                                )
                            )
                            navController.navigate("role_selection")
                            isLoading = false
                        }
                    )
                }
            }
        }
    }

private suspend fun login(
    restaurantEmail: String,
    restaurantPassword: String,
    viewModel: ToTableViewModel,
    onLoginAttemptFailed: (String?) -> Unit,
    onLoginAttemptSuccess: (String) -> Unit
) {
    try {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(restaurantEmail, restaurantPassword)
            .await()
        val restaurantId = viewModel.fetchRestaurantIdByEmail(restaurantEmail)
        if (restaurantId != null) {
            onLoginAttemptSuccess(restaurantId)
        } else {
            FirebaseAuth.getInstance().signOut()
            onLoginAttemptFailed("No restaurant found for this email")
        }
    } catch (e: Exception) {
        onLoginAttemptFailed(e.localizedMessage)
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