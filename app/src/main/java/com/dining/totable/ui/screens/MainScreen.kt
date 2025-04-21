package com.dining.totable.ui.screens

import android.content.pm.ActivityInfo
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.dining.totable.ui.composables.MainScreenTopBar
import com.dining.totable.ui.composables.OrderItem
import com.dining.totable.ui.utils.TopBarOption
import com.dining.totable.utils.DeviceConfigManager
import com.dining.totable.viewmodels.ToTableViewModel

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: ToTableViewModel
) {
    val activity = LocalActivity.current
    val context = LocalContext.current
    val orders by viewModel.orders.observeAsState()
    DisposableEffect(Unit) {
        // Force landscape when entering the screen
        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            // Reset to system default when leaving the screen
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    LaunchedEffect(Unit) {
        viewModel.fetchOrders(DeviceConfigManager(context).getConfiguration()!!.restaurantId)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MainScreenTopBar(
                navController = navController,
                onDropdownMenuItemClicked = { dropdownMenuItemClicked ->
                    if (dropdownMenuItemClicked == TopBarOption.Logout) {
                        DeviceConfigManager(context).logout(navController)
                    } else {
                        //dialog for choose role and new screen for settings
                    }
                }
            )
        }) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues
        ) {
            orders?.let { orderList ->
                items(orderList.flatMap { order -> order.items }) { item ->
                    OrderItem(
                        item = item,
                        hasTellMe = false
                    )
                }
            }
        }
    }
}