package com.dining.totable.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dining.totable.ui.utils.SpecialRequests

@Composable
fun OrderItem(
    itemName: String,
    specialRequests: Map<String, String>,
    price: Double? = null,
    hasTellMe: Boolean? = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(
            width = Dp.Hairline,
            color = Color.Black
        )
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)) {
            ItemNameText(
                itemName = itemName,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            OrderItemSpecialRequests(specialRequests)
            if (hasTellMe == true) {
                OrderItemSpecialRequestCard(containerColor = Color.Blue) {
                    SpecialRequestText(
                        specialRequest = "Tell Me!",
                        specialRequestColor = Color.White
                    )
                }
            }
            if (price != null) {
                ItemNameText(
                    itemName = "€${price}",
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
private fun ItemNameText(
    itemName: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = itemName,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
private fun OrderItemSpecialRequestCard(
    containerColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            width = Dp.Hairline,
            color = Color.Black
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier.padding(bottom = 2.dp)
    ) {
        Box(
            modifier = Modifier.padding(4.dp)
        ) {
            content()
        }
    }
}


@Composable
private fun OrderItemSpecialRequests(specialRequests: Map<String, String>) {
    specialRequests.forEach {
        OrderItemSpecialRequestCard(
            containerColor = when (it.key) {
                "0" -> Color.Red
                "1" -> Color.Green
                "2" -> Color.Yellow
                else -> Color.Gray
            }
        ) {
            val specialRequest = "${SpecialRequests.specialRequestsMap[it.key.toInt()] ?: ""} ${it.value}"
            val specialRequestColor = if (it.key.toInt() == 0) Color.White else Color.Unspecified
            SpecialRequestText(specialRequest, specialRequestColor)
        }
    }
}

@Composable
private fun SpecialRequestText(
    specialRequest: String,
    specialRequestColor: Color
) {
    Text(
        text = specialRequest,
        color = specialRequestColor,
        fontWeight = FontWeight.Bold,
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun OrderItemPreview() {
    OrderItem(
        itemName = "Cheeseburger",
        specialRequests = mapOf(
            "0" to "Pickles",
            "2" to "Lettuce",
            "1" to "Tomato"
        ),
        hasTellMe = true,
        price = 8.99
    )
}