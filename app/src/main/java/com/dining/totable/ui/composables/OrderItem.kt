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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dining.totable.viewmodels.Order

@Composable
fun OrderItem(
    order: Order,
    onClick: () -> Unit
) {
    val isInProgress = order.status == "in-progress"
    val backgroundColor = if (isInProgress) {
        Brush.linearGradient(
            colors = listOf(Color(0xFFFFD54F), Color(0xFFFFA726)),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xFFF0F4F8)),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
        )
    }
    val elevation = if (isInProgress) 16.dp else 8.dp
    val textColor = if (isInProgress) Color(0xFF212121) else Color(0xFF424242)
    val statusBadgeColor = when (order.status) {
        "in-progress" -> Color(0xFF0288D1)
        "pending" -> Color(0xFF78909C)
        else -> Color(0xFF4CAF50)
    }

    val orderTotal = order.items.sumOf { item -> item.price * item.quantity }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(20.dp),
                clip = true
            )
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(width = 1.dp, color = if (isInProgress) Color(0xFFFFA726) else Color(0xFFE0E0E0))
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Table ${order.tableNumber ?: "N/A"}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = textColor
                    )
                    Surface(
                        modifier = Modifier
                            .wrapContentSize(),
                        shape = RoundedCornerShape(16.dp),
                        color = statusBadgeColor,
                        contentColor = Color.White
                    ) {
                        Text(
                            text = order.status.replaceFirstChar { it.uppercase() },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "${item.itemName} (x${item.quantity})",
                                fontWeight = FontWeight.Medium,
                                fontSize = 18.sp,
                                color = textColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (item.specialRequests.isNotEmpty()) {
                                OrderItemSpecialRequestCard(
                                    modifier = Modifier.padding(top = 6.dp)
                                ) {
                                    SpecialRequestText(
                                        specialRequest = item.specialRequests,
                                        specialRequestColor = textColor
                                    )
                                }
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Total: €${String.format("%.2f", orderTotal)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderItemSpecialRequestCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(10.dp))
            .padding(vertical = 2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(width = 0.5.dp, color = Color(0xFFB0BEC5))
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
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