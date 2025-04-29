package com.dining.totable.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dining.totable.viewmodels.Order

@Composable
fun OrderItem(
    item: Order.Item,
    orderStatus: String,
    hasTellMe: Boolean? = false,
    onClick: () -> Unit
) {
    val isInProgress = orderStatus == "in-progress"
    val backgroundColor = if (isInProgress) {
        Brush.linearGradient(
            colors = listOf(Color(0xFFFFCA28), Color(0xFFFFA000)),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.White, Color(0xFFF5F5F5))
        )
    }
    val elevation = if (isInProgress) CardDefaults.cardElevation(defaultElevation = 12.dp) else CardDefaults.cardElevation(defaultElevation = 4.dp)
    val textColor = if (isInProgress) Color.White else Color.Black
    val statusBadgeColor = if (isInProgress) Color(0xFF0277BD) else Color(0xFF757575)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        elevation = elevation,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(width = 1.dp, color = Color(0xFFE0E0E0))
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${item.itemName} (x${item.quantity})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .wrapContentSize(),
                        shape = RoundedCornerShape(12.dp),
                        color = statusBadgeColor,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = orderStatus.replaceFirstChar { it.uppercase() },
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    if (item.specialRequests.isNotEmpty()) {
                        OrderItemSpecialRequestCard(
                            containerColor = Color(0x33FFFFFF),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            SpecialRequestText(
                                specialRequest = item.specialRequests,
                                specialRequestColor = textColor
                            )
                        }
                    }
                    if (hasTellMe == true) {
                        OrderItemSpecialRequestCard(
                            containerColor = Color(0xFF0288D1),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            SpecialRequestText(
                                specialRequest = "Tell Me!",
                                specialRequestColor = Color.White
                            )
                        }
                    }
                }
                Text(
                    text = "€${String.format("%.2f", item.price * item.quantity)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textColor,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
private fun OrderItemSpecialRequestCard(
    containerColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(width = 0.5.dp, color = Color(0xFFB0BEC5))
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            content()
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
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderItemPreview() {
    OrderItem(
        item = Order.Item(
            itemName = "Cheeseburger",
            price = 8.99,
            quantity = 2,
            specialRequests = "No pickles, extra lettuce"
        ),
        orderStatus = "in-progress",
        hasTellMe = true,
        onClick = {}
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrderItemPendingPreview() {
    OrderItem(
        item = Order.Item(
            itemName = "Cheeseburger",
            price = 8.99,
            quantity = 2,
            specialRequests = "No pickles, extra lettuce"
        ),
        orderStatus = "pending",
        hasTellMe = false,
        onClick = {}
    )
}