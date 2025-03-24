package com.dining.totable.ui.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.dining.totable.utils.DeviceConfigManager
import com.dining.totable.ui.utils.TopBarOption
import com.dining.totable.ui.utils.TopBarOptionsToStrRedId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenTopBar(
    navController: NavController,
    onDropdownMenuItemClicked: (TopBarOption) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val deviceConfiguration = DeviceConfigManager(LocalContext.current).getConfiguration()
    var currentDeviceRole by remember { mutableStateOf(deviceConfiguration?.deviceRole) }

    TopAppBar(
        title = { Text(currentDeviceRole?.name ?: "") },
        actions = {
            IconButton(onClick = { menuExpanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More Options")
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                TopBarOptionsToStrRedId.options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(stringResource(option.second)) },
                        onClick = {
                            onDropdownMenuItemClicked(option.first)
                            menuExpanded = false
                        }
                    )
                }
            }
        }
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MainScreenTopBarPreview() {
//    MainScreenTopBar()
}