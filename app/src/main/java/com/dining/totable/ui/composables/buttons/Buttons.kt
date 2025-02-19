package com.dining.totable.ui.composables.buttons

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ToTableButton(
    buttonText: String,
    onClick: () -> Unit
) {

    Button(onClick = { onClick() }) {
        Text(buttonText)
    }
}