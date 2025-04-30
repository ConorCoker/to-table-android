package com.dining.totable.ui.composables

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.dining.totable.ui.utils.TopBarOption
import com.dining.totable.ui.utils.TopBarOptionsToStrRedId
import com.dining.totable.utils.DeviceConfigManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenTopBar(
    navController: NavController,
    onDropdownMenuItemClicked: (TopBarOption) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val deviceConfiguration = DeviceConfigManager(LocalContext.current).getConfiguration()
    var currentDeviceRole by remember { mutableStateOf(deviceConfiguration?.deviceRoleName) }

    TopAppBar(
        title = {
            Text(
                text = currentDeviceRole ?: "",
                style = MaterialTheme.typography.titleLarge
            )
        },
        modifier = Modifier.background(
            Brush.horizontalGradient(
                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
            )
        ),
        actions = {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More Options",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                TopBarOptionsToStrRedId.options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(option.second),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        onClick = {
                            onDropdownMenuItemClicked(option.first)
                            menuExpanded = false
                            if (option.first == TopBarOption.SwitchRole) {
                                navController.navigate("role_selection")
                            }
                        }
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}