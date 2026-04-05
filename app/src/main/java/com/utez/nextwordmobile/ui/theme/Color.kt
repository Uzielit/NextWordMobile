package com.utez.nextwordmobile.ui.theme


import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color


val PrimaryLight = Color(0xFF9788FE)
val WhiteCard = Color(0xFFFFFFFF)
val TextGray = Color(0xFF666666)
val BackgroundGray = Color(0xFFF5F5F5)

val PrimaryBlue = Color(0xFF003399)
val AccentCyan = Color(0xFF00CCFF)

val PrimaryDark = PrimaryBlue

val NextWordGradient = Brush.verticalGradient(
    colors = listOf(PrimaryBlue, AccentCyan)
)

val HorizontalGradient = Brush.horizontalGradient(
    colors = listOf(PrimaryDark, PrimaryLight)
)
