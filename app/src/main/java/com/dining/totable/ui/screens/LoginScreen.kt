package com.dining.totable.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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
    var isLoading by rememberSaveable { mutableStateOf(false) }
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var restaurantEmail by rememberSaveable { mutableStateOf("") }
    var restaurantPassword by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to ToTable",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            ToTableTextField(
                value = restaurantEmail,
                onValueChange = { restaurantEmail = it },
                label = { Text(stringResource(R.string.restaurant_email)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.storefront_24px),
                        contentDescription = "Storefront Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(),
                isError = errorMessage != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            ToTableTextField(
                value = restaurantPassword,
                onValueChange = { restaurantPassword = it },
                label = { Text(stringResource(R.string.password)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.password_24px),
                        contentDescription = "Password Icon",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                isError = errorMessage != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "An error occurred",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
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
            ToTableButton(
                buttonText = stringResource(R.string.login),
                onClick = {
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
                                errorMessage = it ?: context.getString(R.string.login_failed)
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
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
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