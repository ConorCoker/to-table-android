package com.dining.totable.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dining.totable.ui.composables.MainScreenTopBar
import com.dining.totable.ui.composables.OrderItem
import com.dining.totable.ui.utils.TopBarOption
import com.dining.totable.utils.DeviceConfigManager
import com.dining.totable.viewmodels.ToTableViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: ToTableViewModel
) {
    val context = LocalContext.current
    val orders by viewModel.orders.observeAsState(emptyList())
    val config by remember { mutableStateOf(DeviceConfigManager(context).getConfiguration()) }

    LaunchedEffect(Unit) {
        viewModel.fetchOrders(config!!.restaurantId, config!!.deviceRoleId!!)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MainScreenTopBar(
                navController = navController,
                onDropdownMenuItemClicked = { option ->
                    when (option) {
                        TopBarOption.Logout -> DeviceConfigManager(context).logout(navController)
                        TopBarOption.SwitchRole -> navController.navigate("role_selection")
                        else -> {}
                    }
                }
            )
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = orders,
                key = { order -> order.id }
            ) { order ->
                val dismissState = rememberDismissState(
                    confirmStateChange = { value ->
                        if (value == DismissValue.DismissedToStart && order.status != "complete") {
                            viewModel.updateOrderItemStatus(
                                restaurantId = config?.restaurantId ?: "",
                                orderId = order.id,
                                newStatus = "complete"
                            )
                            true
                        } else {
                            false
                        }
                    }
                )
                SwipeToDismiss(
                    state = dismissState,
                    directions = setOf(DismissDirection.EndToStart),
                    background = {},
                    dismissContent = {
                        OrderItem(
                            order = order,
                            onClick = {
                                if (order.status == "pending") {
                                    viewModel.updateOrderItemStatus(
                                        restaurantId = config?.restaurantId ?: "",
                                        orderId = order.id,
                                        newStatus = "in-progress"
                                    )
                                }
                            }
                        )
                    }
                )
            }
        }
    }
}