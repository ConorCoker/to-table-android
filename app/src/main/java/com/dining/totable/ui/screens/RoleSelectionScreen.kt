package com.dining.totable.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dining.totable.utils.DeviceConfigManager
import com.dining.totable.utils.DeviceConfiguration
import com.dining.totable.viewmodels.ToTableViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelectionScreen(
    navController: NavController,
    viewModel: ToTableViewModel
) {
    val context = LocalContext.current
    val roles by viewModel.roles.observeAsState(emptyList())
    val config = DeviceConfigManager(context).getConfiguration()
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (config?.restaurantId != null) {
            viewModel.fetchRoles(config.restaurantId) { success ->
                isLoading = false
                if (!success) {
                    errorMessage = "Failed to load roles"
                }
            }
        } else {
            isLoading = false
            errorMessage = "Restaurant ID not found"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Role") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                errorMessage != null -> {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                roles.isEmpty() -> {
                    Text(
                        text = "No roles available",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(roles) { role ->
                            Button(
                                onClick = {
                                    config?.let {
                                        DeviceConfigManager(context).saveConfiguration(
                                            DeviceConfiguration(
                                                deviceRoleId = role.id,
                                                deviceRoleName = role.name,
                                                restaurantEmail = it.restaurantEmail,
                                                restaurantId = it.restaurantId
                                            )
                                        )
                                        navController.popBackStack()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(role.name)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Role(val id: String, val name: String)