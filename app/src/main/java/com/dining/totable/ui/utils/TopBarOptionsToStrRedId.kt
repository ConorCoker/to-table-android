package com.dining.totable.ui.utils

import com.dining.totable.R

sealed class TopBarOption {
    data object Settings : TopBarOption()
    data object SwitchRole : TopBarOption()
    data object Logout : TopBarOption()
}

object TopBarOptionsToStrRedId {
    // List of possible actions with their string resource IDs
    val options = listOf(
        TopBarOption.Settings to R.string.settings,
        TopBarOption.SwitchRole to R.string.switch_device_role,
        TopBarOption.Logout to R.string.logout
    )
}

