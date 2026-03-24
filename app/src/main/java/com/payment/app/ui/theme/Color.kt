package com.payment.app.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650A4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Due date colors
val DueDate26Color = Color(0xFF1976D2)   // Blue
val DueDate27Color = Color(0xFF388E3C)   // Green
val DueDate31Color = Color(0xFFE64A19)   // Deep Orange

val dueDateColors = mapOf(
    26 to DueDate26Color,
    27 to DueDate27Color,
    31 to DueDate31Color
)

fun getDueDateColor(dueDate: Int): Color {
    return dueDateColors[dueDate] ?: Color(0xFF7B1FA2)
}
